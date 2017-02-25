package com.github.charleslzq.loghub.converter;

import com.aliyun.openservices.log.common.Logs;

import java.util.stream.Collectors;

/**
 * Created by Charles on 2017/2/25.
 */
public class DefaultLogConverter implements LogConverter<LogData> {

    @Override
    public LogData convert(Logs.Log log) {
        return new LogData(
                log.getContentsList().stream()
                        .collect(Collectors.toMap(
                                Logs.Log.Content::getKey,
                                Logs.Log.Content::getValue
                        ))
        );
    }
}
