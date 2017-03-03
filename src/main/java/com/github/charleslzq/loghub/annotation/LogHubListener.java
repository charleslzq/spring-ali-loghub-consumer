package com.github.charleslzq.loghub.annotation;

import com.github.charleslzq.loghub.converter.DefaultLogConverter;
import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.filter.AcceptAllLogFilter;
import com.github.charleslzq.loghub.filter.AcceptAllLogGroupFilter;
import com.github.charleslzq.loghub.filter.LogFilter;
import com.github.charleslzq.loghub.filter.LogGroupFilter;
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

	String[] topics() default {};

	Class<? extends LogGroupFilter> groupFilter() default AcceptAllLogGroupFilter.class;

	Class<? extends LogFilter> logFilter() default AcceptAllLogFilter.class;

	Class<? extends LogConverter> converter() default DefaultLogConverter.class;
}
