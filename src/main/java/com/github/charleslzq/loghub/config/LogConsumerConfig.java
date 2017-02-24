package com.github.charleslzq.loghub.config;

import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.aliyun.openservices.loghub.client.config.LogHubCursorPosition;
import lombok.Data;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
@Data
public class LogConsumerConfig {
    private String endpoint;
    private String project;
    private String store;
    private String accessId;
    private String accessKey;
    private String groupName;
    private LogHubCursorPosition cursorPosition = LogHubCursorPosition.END_CURSOR;
    private int startTime = 0;
    private long fetchIntervalMillis = 200;
    private long heartBeatIntervalMillis = 10000;
    private boolean keepOrder = true;

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
            LogHubConfig config = new LogHubConfig(
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
            config.setDataFetchIntervalMillis(fetchIntervalMillis);
            return config;
        } else {
            LogHubConfig config = new LogHubConfig(
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
            config.setDataFetchIntervalMillis(fetchIntervalMillis);
            return config;
        }
    }

    public LogHubConfig generateLogHubConfig(
            String consumerName
    ) {
        return generateLogHubConfig(
                groupName,
                consumerName,
                cursorPosition,
                startTime,
                fetchIntervalMillis,
                heartBeatIntervalMillis,
                keepOrder
        );
    }
}
