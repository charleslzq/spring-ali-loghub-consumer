package com.github.charleslzq.loghub.converter;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.Logs;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public class DefaultLogGroupConverter implements LogGroupConverter {

    @Override
    public List<Message<LogData>> convert(LogGroupData logGroupData) {
        Logs.LogGroup logGroup = logGroupData.GetLogGroup();
        Map<String, String> headers = new HashMap<>();
        headers.put(LogGroupHeaders.SOURCE.getKey(), logGroup.getSource());
        headers.put(LogGroupHeaders.TOPIC.getKey(), logGroup.getTopic());
        headers.put(LogGroupHeaders.MACHINE_UUID.getKey(), logGroup.getMachineUUID());
        headers.put(LogGroupHeaders.CATEGORY.getKey(), logGroup.getCategory());

        return logGroup.getLogsList().stream()
                .map(this::convert)
                .map(map -> MessageBuilder
                        .withPayload(map)
                        .copyHeaders(headers)
                        .build()
                ).collect(Collectors.toList());
    }

    private LogData convert(Logs.Log log) {
        return new LogData(
                log.getContentsList().stream()
                        .collect(Collectors.toMap(
                                Logs.Log.Content::getKey,
                                Logs.Log.Content::getValue
                        ))
        );
    }
}
