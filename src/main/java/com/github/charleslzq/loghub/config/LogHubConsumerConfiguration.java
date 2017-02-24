package com.github.charleslzq.loghub.config;

import com.aliyun.openservices.log.common.LogGroupData;
import com.github.charleslzq.loghub.converter.DefaultLogGroupConverter;
import com.github.charleslzq.loghub.converter.LogGroupConverter;
import com.github.charleslzq.loghub.listener.LogHubListenerContainer;
import com.github.charleslzq.loghub.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.messaging.Message;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Autowired(required = false)
    private List<TaskDecorator> taskDecorators;

    @Bean
    @ConditionalOnMissingBean
    public AsyncListenableTaskExecutor logHubListenerRunner() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setTaskDecorator(
                taskDecorators == null || taskDecorators.size() == 0 ? null :
                        runnable -> {
                            Runnable task = runnable;
                            for (TaskDecorator decorator : taskDecorators) {
                                task = decorator.decorate(task);
                            }

                            return task;
                        }
        );
        return taskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean
    public LogGroupConverter logGroupConverter() {
        return new DefaultLogGroupConverter();
    }

    @Bean
    public LogHubListenerContainer logHubListenerContainer() {
        LogHubListenerContainer logHubListenerContainer = new LogHubListenerContainer(
                logHubListenerRunner(),
                logGroupConverter(),
                logHubConsumerProperties
        );
        logHubListenerContainer.setListenerRegistry(
                groupName -> Collections.singletonList(new SimplePrinter())
        );
        return logHubListenerContainer;
    }

    public static class SimplePrinter implements MessageListener {

        @Override
        public void onMessage(Message<Map<String, String>> message) {
            System.out.println(message);
        }
    }

}
