package com.github.charleslzq.loghub.config;

import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.filter.LogFilter;
import com.github.charleslzq.loghub.filter.LogGroupFilter;
import com.github.charleslzq.loghub.listener.MessageListener;

/**
 * Created by Charles on 2017/2/25.
 */
public interface LogHubListenerEndpoint<T> {
	String getConfigName();

	String getName();

	LogGroupFilter getLogGroupFilter();

	LogFilter getLogFilter();

	LogConverter<T> getConverter();

	MessageListener<T> getListener();
}
