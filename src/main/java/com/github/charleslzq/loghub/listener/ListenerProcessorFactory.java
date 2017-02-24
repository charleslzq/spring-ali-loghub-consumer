package com.github.charleslzq.loghub.listener;

import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessorFactory;
import com.github.charleslzq.loghub.converter.LogGroupConverter;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public class ListenerProcessorFactory implements ILogHubProcessorFactory{
    private final MessageListener messageListener;
    private final LogGroupConverter logGroupConverter;

    public ListenerProcessorFactory(MessageListener messageListener, LogGroupConverter logGroupConverter) {
        this.messageListener = messageListener;
        this.logGroupConverter = logGroupConverter;
    }

    @Override
    public ILogHubProcessor generatorProcessor() {
        return new ListenerProcessor(messageListener, logGroupConverter);
    }
}
