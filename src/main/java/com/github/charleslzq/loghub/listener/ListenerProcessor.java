package com.github.charleslzq.loghub.listener;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.loghub.client.ILogHubCheckPointTracker;
import com.aliyun.openservices.loghub.client.exceptions.LogHubCheckPointException;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;
import com.github.charleslzq.loghub.converter.LogGroupConverter;

import java.util.List;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
class ListenerProcessor implements ILogHubProcessor {
    private final MessageListener messageListener;
    private final LogGroupConverter converter;
    private int sharId;
    private long lastCheckTime = 0;

    ListenerProcessor(MessageListener messageListener, LogGroupConverter converter) {
        this.messageListener = messageListener;
        this.converter = converter;
    }

    @Override
    public void initialize(int shardId) {
        this.sharId = shardId;
    }

    @Override
    public String process(List<LogGroupData> list, ILogHubCheckPointTracker iLogHubCheckPointTracker) {
        list.stream()
                .map(converter::convert)
                .flatMap(List::stream)
                .forEach(messageListener::onMessage);

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

    @Override
    public void shutdown(ILogHubCheckPointTracker iLogHubCheckPointTracker) {
        try {
            iLogHubCheckPointTracker.saveCheckPoint(true);
        } catch (LogHubCheckPointException e) {
            e.printStackTrace();
        }
    }
}
