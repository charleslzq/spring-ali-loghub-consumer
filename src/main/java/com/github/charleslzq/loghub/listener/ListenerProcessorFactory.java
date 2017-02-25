package com.github.charleslzq.loghub.listener;

import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessorFactory;
import com.github.charleslzq.loghub.converter.LogConverter;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
class ListenerProcessorFactory<T> implements ILogHubProcessorFactory {
    private final MessageListener<T> messageListener;
    private final LogConverter<T> logConverter;

    ListenerProcessorFactory(MessageListener<T> messageListener, LogConverter<T> logConverter) {
        this.messageListener = messageListener;
        this.logConverter = logConverter;
    }

    @Override
    public ILogHubProcessor generatorProcessor() {
        return new ListenerProcessor<>(messageListener, logConverter);
    }
}
