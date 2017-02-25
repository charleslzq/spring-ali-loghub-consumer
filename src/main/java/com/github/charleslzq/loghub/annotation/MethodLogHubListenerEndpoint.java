package com.github.charleslzq.loghub.annotation;

import java.lang.reflect.Method;

/**
 * Created by Charles on 2017/2/25.
 */
public class MethodLogHubListenerEndpoint {
    private final Object bean;
    private final Method method;

    public MethodLogHubListenerEndpoint(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }
}
