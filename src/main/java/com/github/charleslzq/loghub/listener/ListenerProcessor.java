package com.github.charleslzq.loghub.listener;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.loghub.client.ILogHubCheckPointTracker;
import com.aliyun.openservices.loghub.client.exceptions.LogHubCheckPointException;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;
import com.github.charleslzq.loghub.converter.LogConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
class ListenerProcessor<T> implements ILogHubProcessor {
    private final MessageListener<T> messageListener;
    private final LogConverter<T> logConverter;
    private int sharId;
    private long lastCheckTime = 0;

    ListenerProcessor(MessageListener messageListener, LogConverter<T> logConverter) {
        this.messageListener = messageListener;
        this.logConverter = logConverter;
    }

    @Override
    public void initialize(int shardId) {
        this.sharId = shardId;
    }

    @Override
    public String process(List<LogGroupData> list, ILogHubCheckPointTracker iLogHubCheckPointTracker) {
        try {
            list.stream()
                    .map(this::convert)
                    .flatMap(List::stream)
                    .forEach(messageListener::onMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return iLogHubCheckPointTracker.getCheckPoint();
        }

        long curTime = System.currentTimeMillis();
        if (curTime - lastCheckTime > 60 * 1000) {
            try {
                iLogHubCheckPointTracker.saveCheckPoint(true);
            } catch (LogHubCheckPointException e) {
                e.printStackTrace();
            }
            lastCheckTime = curTime;
        } else {
            try {
                iLogHubCheckPointTracker.saveCheckPoint(false);
            } catch (LogHubCheckPointException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private List<Message<T>> convert(LogGroupData logGroupData) {
        Logs.LogGroup logGroup = logGroupData.GetLogGroup();
        Map<String, String> headers = new HashMap<>();
        headers.put(LogGroupHeaders.SOURCE.getKey(), logGroup.getSource());
        headers.put(LogGroupHeaders.TOPIC.getKey(), logGroup.getTopic());
        headers.put(LogGroupHeaders.MACHINE_UUID.getKey(), logGroup.getMachineUUID());
        headers.put(LogGroupHeaders.CATEGORY.getKey(), logGroup.getCategory());
        headers.put(LogGroupHeaders.SHARD.getKey(), sharId + "");

        return logGroup.getLogsList().stream()
                .map(log -> MessageBuilder
                        .withPayload(this.logConverter.convert(log))
                        .copyHeaders(headers)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void shutdown(ILogHubCheckPointTracker iLogHubCheckPointTracker) {
        try {
            iLogHubCheckPointTracker.saveCheckPoint(true);
        } catch (LogHubCheckPointException e) {
            e.printStackTrace();
        }
    }
}
