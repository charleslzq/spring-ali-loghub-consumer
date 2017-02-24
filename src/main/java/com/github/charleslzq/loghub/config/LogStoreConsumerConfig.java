package com.github.charleslzq.loghub.config;

import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.aliyun.openservices.loghub.client.config.LogHubCursorPosition;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
@Data
public class LogStoreConsumerConfig {
    private String endpoint;
    private String project;
    private String store;

    private String accessId;
    private String accessKey;

    private List<GroupConsumerConfig> consumers = new ArrayList<>();

    private LogHubConfig generateLogHubConfig(
            String groupName,
            String consumerName,
            LogHubCursorPosition cursorPosition,
            int startTime,
            long fetchIntervalMillis,
            long heartBeatIntervalMillis,
            boolean keepOrder
    ) {
        if (LogHubCursorPosition.SPECIAL_TIMER_CURSOR == cursorPosition) {
            return new LogHubConfig(
                    groupName,
                    consumerName,
                    endpoint,
                    project,
                    store,
                    accessId,
                    accessKey,
                    startTime,
                    heartBeatIntervalMillis,
                    keepOrder
            );
        } else {
            return new LogHubConfig(
                    groupName,
                    consumerName,
                    endpoint,
                    project,
                    store,
                    accessId,
                    accessKey,
                    cursorPosition,
                    heartBeatIntervalMillis,
                    keepOrder
            );
        }
    }

    public LogHubConfig generateLogHubConfig(
            String groupName,
            String consumerName
    ) {
        return consumers.stream()
                .filter(consumer -> groupName.equals(consumer.getGroupName()))
                .findAny()
                .map(groupConsumerConfig -> generateLogHubConfig(
                        groupName,
                        consumerName,
                        groupConsumerConfig.getCursorPosition(),
                        groupConsumerConfig.getStartTime(),
                        groupConsumerConfig.getFetchIntervalMillis(),
                        groupConsumerConfig.getHeartBeatIntervalMillis(),
                        groupConsumerConfig.isKeepOrder()
                )).orElseThrow(
                        () -> new IllegalArgumentException("Can't find configuration for group " + groupName)
                );
    }
}
