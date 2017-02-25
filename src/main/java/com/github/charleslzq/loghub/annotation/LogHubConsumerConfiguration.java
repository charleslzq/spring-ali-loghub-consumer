package com.github.charleslzq.loghub.annotation;

import com.github.charleslzq.loghub.config.ClientWorkerContainerFactory;
import com.github.charleslzq.loghub.config.LogHubConsumerProperties;
import com.github.charleslzq.loghub.converter.LogData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
@Configuration
@EnableConfigurationProperties(
        value = {
                LogHubConsumerProperties.class
        }
)
public class LogHubConsumerConfiguration {

    @Autowired
    private LogHubConsumerProperties logHubConsumerProperties;

    @Bean
    public ClientWorkerContainerFactory clientWorkerContainerFactory() {
        return new ClientWorkerContainerFactory(logHubConsumerProperties);
    }

    @Bean
    public LogHubListenerBeanPostProcessor logHubListenerBeanPostProcessor() {
        return new LogHubListenerBeanPostProcessor(clientWorkerContainerFactory());
    }


    @Component
    public static class SimplePrinter{
        @LogHubListener(configName = "test", topics = "ms-search")
        public void print(LogData message) {
            System.out.println(message);
        }
    }

}
