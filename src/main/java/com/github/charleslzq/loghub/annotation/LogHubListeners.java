package com.github.charleslzq.loghub.annotation;

import java.lang.annotation.*;

/**
 * Created by Charles on 2017/2/25.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogHubListeners {
    LogHubListener[] value();
}
