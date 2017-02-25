package com.github.charleslzq.loghub.config;

import com.github.charleslzq.loghub.annotation.LogHubListener;
import com.github.charleslzq.loghub.converter.DefaultLogConverter;
import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.listener.LogGroupHeaders;
import com.github.charleslzq.loghub.listener.MessageListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Charles on 2017/2/25.
 */
public class MethodLogHubListenerEndpoint implements LogHubListenerEndpoint {
    private final Object bean;
    private final Method method;
    private final MessageHandlerMethodFactory messageHandlerMethodFactory;
    private final LogHubListener annotation;
    private final InvocableHandlerMethod invocableHandlerMethod;
    private final Set<String> topics = new HashSet<>();

    public MethodLogHubListenerEndpoint(Object bean, Method method, MessageHandlerMethodFactory messageHandlerMethodFactory, LogHubListener annotation) {
        this.bean = bean;
        this.method = method;
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
        this.annotation = annotation;
        this.invocableHandlerMethod = messageHandlerMethodFactory.createInvocableHandlerMethod(bean, method);
        this.topics.addAll(Arrays.asList(annotation.topics()));
    }

    @Override
    public String getConfigName() {
        return annotation.configName();
    }

    @Override
    public String getName() {
        return bean.getClass().getName() + "$" + method.getName();
    }

    @Override
    public LogConverter getConverter() {
        return new DefaultLogConverter();
    }

    @Override
    public MessageListener getListener() {
        if (this.topics.size() == 0) {
            return this::invokeMethod;
        } else {
            return message -> {
                if (topics.contains(message.getHeaders().get(LogGroupHeaders.TOPIC.getKey()))) {
                    this.invokeMethod(message);
                }
            };
        }
    }

    private void invokeMethod(Message message) {
        try {
            this.invocableHandlerMethod.invoke(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
