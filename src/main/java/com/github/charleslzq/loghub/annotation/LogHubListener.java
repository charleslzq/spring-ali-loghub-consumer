package com.github.charleslzq.loghub.annotation;

import org.springframework.messaging.handler.annotation.MessageMapping;

import java.lang.annotation.*;

/**
 * Created by Charles on 2017/2/25.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
@Repeatable(LogHubListeners.class)
public @interface LogHubListener {
    String configName();

    String name() default "";

    String[] topics() default {};
}
