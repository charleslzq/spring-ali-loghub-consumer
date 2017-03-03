package com.github.charleslzq.loghub.config;

import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.github.charleslzq.loghub.listener.ClientWorkerContainer;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * Created by Charles on 2017/2/25.
 */
public class ClientWorkerContainerFactory {
	private final LogHubConsumerProperties logHubConsumerProperties;

	public ClientWorkerContainerFactory(LogHubConsumerProperties logHubConsumerProperties) {
		this.logHubConsumerProperties = logHubConsumerProperties;
	}

	public <T> ClientWorkerContainer<T> createClientWorkerContainer(LogHubListenerEndpoint<T> endpoint) {
		if (!logHubConsumerProperties.getConfigs().containsKey(endpoint.getConfigName())) {
			throw new IllegalArgumentException("Can't find configuration for " + endpoint.getConfigName());
		}
		LogConsumerConfig config = logHubConsumerProperties.getConfigs().get(endpoint.getConfigName());
		LogHubConfig logHubConfig = config.generateLogHubConfig(endpoint.getName());
		ClientWorkerContainer<T> container = new ClientWorkerContainer<>(
				endpoint.getLogGroupFilter(),
				endpoint.getLogFilter(),
				endpoint.getConverter(),
				new SimpleAsyncTaskExecutor(),
				logHubConfig);
		container.setMessageListener(endpoint.getListener());
		return container;
	}
}
