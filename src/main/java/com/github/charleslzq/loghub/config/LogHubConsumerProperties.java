package com.github.charleslzq.loghub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
@Data
@ConfigurationProperties(
		prefix = "spring.ali.log-hub.consumer"
)
public class LogHubConsumerProperties {
	private Map<String, LogConsumerConfig> configs;
}
