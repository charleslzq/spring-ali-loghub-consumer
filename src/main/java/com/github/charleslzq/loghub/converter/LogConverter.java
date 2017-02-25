package com.github.charleslzq.loghub.converter;

import com.aliyun.openservices.log.common.Logs;

/**
 * Created by Charles on 2017/2/25.
 */
public interface LogConverter<T> {
    T convert(Logs.Log log);

    void init();
}
