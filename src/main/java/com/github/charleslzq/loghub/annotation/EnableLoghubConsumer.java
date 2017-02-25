package com.github.charleslzq.loghub.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Charles on 2017/2/25.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogHubConsumerConfiguration.class)
public @interface EnableLoghubConsumer {
}
