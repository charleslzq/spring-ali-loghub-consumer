package com.github.charleslzq.loghub.config;

import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.listener.MessageListener;

/**
 * Created by Charles on 2017/2/25.
 */
public interface LogHubListenerEndpoint<T> {
    String getConfigName();

    String getName();

    LogConverter<T> getConverter();

    MessageListener<T> getListener();
}
