package com.github.charleslzq.loghub.listener;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public enum LogGroupHeaders {
	SOURCE("source"),
	TOPIC("topic"),
	MACHINE_UUID("machine_uuid"),
	CATEGORY("category"),
	SHARD("shard");

	private final String key;

	LogGroupHeaders(String key) {
		this.key = this.getClass().getName() + "." + key;
	}

	public String getKey() {
		return key;
	}
}
