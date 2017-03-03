package com.github.charleslzq.loghub.annotation;

import com.github.charleslzq.loghub.config.ClientWorkerContainerFactory;
import com.github.charleslzq.loghub.config.LogHubConsumerProperties;
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
	public ClientWorkerContainerFactory clientWorkerContainerFactory() {
		return new ClientWorkerContainerFactory(logHubConsumerProperties);
	}

	@Bean
	public LogHubListenerBeanPostProcessor logHubListenerBeanPostProcessor() {
		return new LogHubListenerBeanPostProcessor(clientWorkerContainerFactory());
	}
}
