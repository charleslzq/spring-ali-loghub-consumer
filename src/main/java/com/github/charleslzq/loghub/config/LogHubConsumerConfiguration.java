package com.github.charleslzq.loghub.config;

import com.github.charleslzq.loghub.converter.DefaultLogConverter;
import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.converter.LogData;
import com.github.charleslzq.loghub.listener.ClientWorkerContainer;
import com.github.charleslzq.loghub.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ClientWorkerContainer<LogData> clientWorkerContainer() {
        ClientWorkerContainerFactory factory = new ClientWorkerContainerFactory(logHubConsumerProperties);
        SimplePrinter printer = new SimplePrinter();
        return factory.createClientWorkerContainer(printer);
    }


    public static class SimplePrinter implements LogHubListenerEndpoint<LogData> {

        @Override
        public String getConfigName() {
            return "test";
        }

        @Override
        public String getName() {
            return this.getClass().getName();
        }

        @Override
        public LogConverter<LogData> getConverter() {
            return new DefaultLogConverter();
        }

        @Override
        public MessageListener<LogData> getListener() {
            return System.out::println;
        }
    }

}
