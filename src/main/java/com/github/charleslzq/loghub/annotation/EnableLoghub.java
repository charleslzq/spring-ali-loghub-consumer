package com.github.charleslzq.loghub.annotation;

import java.lang.annotation.*;

/**
 * Created by Charles on 2017/2/25.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableLoghub {
}
