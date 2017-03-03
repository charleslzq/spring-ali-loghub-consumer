package com.github.charleslzq.loghub.config;

import com.github.charleslzq.loghub.annotation.LogHubListener;
import com.github.charleslzq.loghub.converter.DefaultLogConverter;
import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.filter.*;
import com.github.charleslzq.loghub.listener.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.lang.reflect.Method;

/**
 * Created by Charles on 2017/2/25.
 */
@Slf4j
public class MethodLogHubListenerEndpoint implements LogHubListenerEndpoint {
	private final Object bean;
	private final Method method;
	private final MessageHandlerMethodFactory messageHandlerMethodFactory;
	private final LogHubListener annotation;
	private final InvocableHandlerMethod invocableHandlerMethod;

	public MethodLogHubListenerEndpoint(Object bean, Method method, MessageHandlerMethodFactory messageHandlerMethodFactory, LogHubListener annotation) {
		this.bean = bean;
		this.method = method;
		this.messageHandlerMethodFactory = messageHandlerMethodFactory;
		this.annotation = annotation;
		this.invocableHandlerMethod = messageHandlerMethodFactory.createInvocableHandlerMethod(bean, method);
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
	public LogGroupFilter getLogGroupFilter() {
		LogGroupFilter defaultGroupFilter = annotation.topics().length > 0 ?
				new TopicsFilter(annotation.topics()) :
				new AcceptAllLogGroupFilter();
		Class<? extends LogGroupFilter> annotationFilterClass
				= annotation.groupFilter();
		if (AcceptAllLogGroupFilter.class.isAssignableFrom(annotationFilterClass)) {
			return defaultGroupFilter;
		} else {
			try {
				LogGroupFilter annotationFilter = annotationFilterClass.newInstance();
				return logGroupData -> defaultGroupFilter.accept(logGroupData)
						&& annotationFilter.accept(logGroupData);
			} catch (IllegalAccessException | InstantiationException e) {
				log.warn("Can't create class " + annotationFilterClass.getName(), e);
				return defaultGroupFilter;
			}
		}
	}

	@Override
	public LogFilter getLogFilter() {
		try {
			return annotation.logFilter().newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			log.warn("Can't create class " + annotation.logFilter().getName(), e);
			return new AcceptAllLogFilter();
		}
	}

	@Override
	public LogConverter getConverter() {
		Class<? extends LogConverter> converterClass = annotation.converter();
		try {
			return converterClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return new DefaultLogConverter();
	}

	@Override
	public MessageListener getListener() {
		return this::invokeMethod;
	}

	private void invokeMethod(Message message) {
		try {
			this.invocableHandlerMethod.invoke(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
