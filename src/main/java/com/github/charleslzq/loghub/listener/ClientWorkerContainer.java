package com.github.charleslzq.loghub.listener;

import com.aliyun.openservices.loghub.client.ClientWorker;
import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.aliyun.openservices.loghub.client.exceptions.LogHubClientWorkerException;
import com.github.charleslzq.loghub.converter.LogConverter;
import com.github.charleslzq.loghub.filter.LogFilter;
import com.github.charleslzq.loghub.filter.LogGroupFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.task.AsyncListenableTaskExecutor;

/**
 * Created by Charles on 2017/2/25.
 */
public class ClientWorkerContainer<T> implements ListenerContainer<T> {
	private final LogGroupFilter logGroupFilter;
	private final LogFilter logFilter;
	private final LogConverter<T> logConverter;
	private final AsyncListenableTaskExecutor taskExecutor;
	private final LogHubConfig logHubConfig;
	@Setter
	private MessageListener<T> messageListener;
	@Getter
	@Setter
	private boolean running = false;
	@Getter
	@Setter
	private boolean autoStartup = true;
	@Getter
	@Setter
	private int phase = 0;
	private Object lifeCycleMonitor = new Object();

	public ClientWorkerContainer(LogGroupFilter logGroupFilter, LogFilter logFilter, LogConverter<T> converter, AsyncListenableTaskExecutor taskExecutor, LogHubConfig logHubConfig) {
		this.logGroupFilter = logGroupFilter;
		this.logFilter = logFilter;
		this.logConverter = converter;
		this.taskExecutor = taskExecutor;
		this.logHubConfig = logHubConfig;
	}

	protected void doStart() {
		ListenerProcessorFactory<T> listenerProcessorFactory = new ListenerProcessorFactory<>(messageListener, logGroupFilter, logFilter, logConverter);
		try {
			ClientWorker clientWorker = new ClientWorker(listenerProcessorFactory, logHubConfig);
			taskExecutor.submitListenable(clientWorker);
		} catch (LogHubClientWorkerException e) {
			e.printStackTrace();
		}
	}

	protected void doStop(Runnable runnable) {
		runnable.run();
	}

	@Override
	public final void stop(Runnable runnable) {
		synchronized (lifeCycleMonitor) {
			if (this.isRunning()) {
				this.doStop(runnable);
				this.setRunning(false);
			}
		}
	}


	@Override
	public final void start() {
		synchronized (lifeCycleMonitor) {
			if (!this.isRunning()) {
				this.doStart();
				this.setRunning(true);
			}
		}
	}

	@Override
	public final void stop() {
		synchronized (lifeCycleMonitor) {
			if (this.isRunning()) {
				this.doStop(() -> {
				});
				this.setRunning(false);
			}
		}
	}
}
