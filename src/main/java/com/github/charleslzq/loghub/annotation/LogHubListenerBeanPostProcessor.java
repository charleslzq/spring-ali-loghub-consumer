package com.github.charleslzq.loghub.annotation;

import com.github.charleslzq.loghub.config.ClientWorkerContainerFactory;
import com.github.charleslzq.loghub.config.LogHubListenerEndpoint;
import com.github.charleslzq.loghub.config.MethodLogHubListenerEndpoint;
import com.github.charleslzq.loghub.listener.ClientWorkerContainer;
import lombok.Setter;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.handler.annotation.support.*;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by Charles on 2017/2/25.
 */
public class LogHubListenerBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent> {
    private final ClientWorkerContainerFactory clientWorkerContainerFactory;
    private final Set<LogHubListenerEndpoint> endpoints = new HashSet<>();
    private final Map<String, ClientWorkerContainer> containerMap = new ConcurrentHashMap<>();
    @Setter
    private BeanFactory beanFactory;
    private LogHubMessageHandlerMethodFactory messageHandlerMethodFactory = new LogHubMessageHandlerMethodFactory();
    private boolean refreshed = false;

    public LogHubListenerBeanPostProcessor(ClientWorkerContainerFactory clientWorkerContainerFactory) {
        this.clientWorkerContainerFactory = clientWorkerContainerFactory;
    }

    @Override
    public int getOrder() {
        return 2147483647;
    }

    @Override
    public void afterSingletonsInstantiated() {
        synchronized (containerMap) {
            endpoints.forEach(endpoint -> {
                ClientWorkerContainer container = clientWorkerContainerFactory.createClientWorkerContainer(endpoint);
                containerMap.putIfAbsent(endpoint.getName(), container);
                if (this.refreshed || container.isAutoStartup()) {
                    container.start();
                }
            });
        }

    }

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class targetClass = AopUtils.isAopProxy(o) ? AopUtils.getTargetClass(o) : o.getClass();
        Stream.of(targetClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(LogHubListener.class) || method.isAnnotationPresent(LogHubListeners.class))
                .forEach(method ->
                        Stream.of(method.getAnnotationsByType(LogHubListener.class))
                                .forEach(logHubListener -> process(logHubListener, method, o))
                );
        return o;
    }

    private void process(LogHubListener logHubListener, Method method, Object bean) {
        Method methodToUse = checkProxy(method, bean);
        MethodLogHubListenerEndpoint endpoint = new MethodLogHubListenerEndpoint(bean, methodToUse, this.messageHandlerMethodFactory, logHubListener);
        endpoints.add(endpoint);
    }

    private Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class[] ex = ((Advised) bean).getProxiedInterfaces();
                Class[] var5 = ex;
                int var6 = ex.length;
                int var7 = 0;

                while (var7 < var6) {
                    Class iface = var5[var7];

                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (NoSuchMethodException var10) {
                        ++var7;
                    }
                }
            } catch (SecurityException var11) {
                ReflectionUtils.handleReflectionException(var11);
            } catch (NoSuchMethodException var12) {
                throw new IllegalStateException(String.format("@KafkaListener method \'%s\' found on bean target class \'%s\', but not found in any interface(s) for bean JDK proxy. Either pull the method up to an interface or switch to subclass (CGLIB) proxies by setting proxy-target-class/proxyTargetClass attribute to \'true\'", new Object[]{methodArg.getName(), methodArg.getDeclaringClass().getSimpleName()}), var12);
            }
        }

        return method;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        refreshed = true;
    }

    private class LogHubMessageHandlerMethodFactory implements MessageHandlerMethodFactory {
        private MessageHandlerMethodFactory messageHandlerMethodFactory;

        private LogHubMessageHandlerMethodFactory() {
        }

        public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
            return this.getMessageHandlerMethodFactory().createInvocableHandlerMethod(bean, method);
        }

        private MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
            if (this.messageHandlerMethodFactory == null) {
                this.messageHandlerMethodFactory = this.createDefaultMessageHandlerMethodFactory();
            }

            return this.messageHandlerMethodFactory;
        }

        public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory kafkaHandlerMethodFactory1) {
            this.messageHandlerMethodFactory = kafkaHandlerMethodFactory1;
        }

        private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {
            DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
            defaultFactory.setBeanFactory(LogHubListenerBeanPostProcessor.this.beanFactory);
            ConfigurableBeanFactory cbf = LogHubListenerBeanPostProcessor.this.beanFactory instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory) LogHubListenerBeanPostProcessor.this.beanFactory : null;
            DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
            defaultFactory.setConversionService(conversionService);
            List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
            argumentResolvers.add(new HeaderMethodArgumentResolver(conversionService, cbf));
            argumentResolvers.add(new HeadersMethodArgumentResolver());
            final GenericMessageConverter messageConverter = new GenericMessageConverter(conversionService);
            argumentResolvers.add(new MessageMethodArgumentResolver(messageConverter));
            argumentResolvers.add(new PayloadArgumentResolver(messageConverter));
            defaultFactory.setArgumentResolvers(argumentResolvers);
            defaultFactory.afterPropertiesSet();
            return defaultFactory;
        }
    }
}
