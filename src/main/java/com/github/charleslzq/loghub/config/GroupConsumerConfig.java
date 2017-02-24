package com.github.charleslzq.loghub.config;

import com.aliyun.openservices.loghub.client.config.LogHubCursorPosition;
import lombok.Data;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
@Data
public class GroupConsumerConfig {
    private String groupName;
    private LogHubCursorPosition cursorPosition = LogHubCursorPosition.END_CURSOR;
    private int startTime = 0;
    private long fetchIntervalMillis = 200;
    private long heartBeatIntervalMillis = 10000;
    private boolean keepOrder = true;
}
