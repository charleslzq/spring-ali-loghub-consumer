package com.github.charleslzq.loghub.config;

import com.github.charleslzq.loghub.annotation.LogHubListener;
import com.github.charleslzq.loghub.converter.DefaultLogConverter;
import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.listener.MessageListener;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.lang.reflect.Method;

/**
 * Created by Charles on 2017/2/25.
 */
public class MethodLogHubListenerEndpoint implements LogHubListenerEndpoint {
    private final Object bean;
    private final Method method;
    private final MessageHandlerMethodFactory messageHandlerMethodFactory;
    private final LogHubListener annotaion;
    private final InvocableHandlerMethod invocableHandlerMethod;

    public MethodLogHubListenerEndpoint(Object bean, Method method, MessageHandlerMethodFactory messageHandlerMethodFactory, LogHubListener annotaion) {
        this.bean = bean;
        this.method = method;
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
        this.annotaion = annotaion;
        this.invocableHandlerMethod = messageHandlerMethodFactory.createInvocableHandlerMethod(bean, method);
    }

    @Override
    public String getConfigName() {
        return annotaion.configName();
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
        return message -> {
            try {
                this.invocableHandlerMethod.invoke(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
