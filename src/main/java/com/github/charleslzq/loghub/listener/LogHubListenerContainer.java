package com.github.charleslzq.loghub.listener;

import com.aliyun.openservices.loghub.client.ClientWorker;
import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.aliyun.openservices.loghub.client.exceptions.LogHubClientWorkerException;
import com.github.charleslzq.loghub.config.LogHubConsumerProperties;
import com.github.charleslzq.loghub.converter.LogGroupConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.task.AsyncListenableTaskExecutor;

import java.util.List;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public class LogHubListenerContainer implements ListenerContainer {
    @Setter
    private ListenerRegistry listenerRegistry;
    private final AsyncListenableTaskExecutor taskExecutor;
    private final LogGroupConverter converter;
    private final LogHubConsumerProperties logHubConsumerProperties;
    @Getter @Setter
    private boolean autoStartup = true;
    @Getter @Setter
    private boolean running = false;
    @Getter @Setter
    private int phase = 0;
    private Object lifeCycleMonitor = new Object();

    public LogHubListenerContainer(AsyncListenableTaskExecutor taskExecutor, LogGroupConverter converter, LogHubConsumerProperties logHubConsumerProperties) {
        this.taskExecutor = taskExecutor;
        this.converter = converter;
        this.logHubConsumerProperties = logHubConsumerProperties;
    }

    @Override
    public void stop(Runnable runnable) {
        if (this.isRunning()) {
            synchronized (this.lifeCycleMonitor) {
                this.doStop(runnable);
                this.setRunning(false);
            }
        }
    }

    private void doStop(Runnable runnable) {
    }

    @Override
    public void start() {
        synchronized (this.lifeCycleMonitor) {
            this.doStart();
            this.setRunning(true);
        }
    }

    private void doStart() {
        if (!this.isRunning()) {
            logHubConsumerProperties.getStores().stream()
                    .forEach(logStoreConsumerConfig -> {
                        logStoreConsumerConfig.getConsumers().stream()
                                .forEach(groupConsumerConfig -> {
                                    String groupName = groupConsumerConfig.getGroupName();
                                    List<MessageListener> listeners = listenerRegistry.findListener(groupName);
                                    listeners.stream()
                                            .forEach(messageListener -> {
                                                LogHubConfig config = logStoreConsumerConfig.generateLogHubConfig(groupName, messageListener.name());
                                                ListenerProcessorFactory factory = new ListenerProcessorFactory(messageListener, converter);
                                                try {
                                                    ClientWorker worker = new ClientWorker(factory, config);
                                                    taskExecutor.submitListenable(worker);
                                                } catch (LogHubClientWorkerException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                        });
                    });
        }
    }

    @Override
    public void stop() {
        synchronized (this.lifeCycleMonitor) {
            this.doStop(()->{});
            this.setRunning(false);
        }
    }
}
