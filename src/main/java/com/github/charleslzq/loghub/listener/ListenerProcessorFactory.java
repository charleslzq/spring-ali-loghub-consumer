package com.github.charleslzq.loghub.listener;

import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessorFactory;
import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.filter.LogFilter;
import com.github.charleslzq.loghub.filter.LogGroupFilter;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
class ListenerProcessorFactory<T> implements ILogHubProcessorFactory {
	private final MessageListener<T> messageListener;
	private final LogGroupFilter logGroupFilter;
	private final LogFilter logFilter;
	private final LogConverter<T> logConverter;

	ListenerProcessorFactory(MessageListener<T> messageListener, LogGroupFilter logGroupFilter, LogFilter logFilter, LogConverter<T> logConverter) {
		this.messageListener = messageListener;
		this.logGroupFilter = logGroupFilter;
		this.logFilter = logFilter;
		this.logConverter = logConverter;
	}

	@Override
	public ILogHubProcessor generatorProcessor() {
		return new ListenerProcessor<>(messageListener, logGroupFilter, logFilter, logConverter);
	}
}
