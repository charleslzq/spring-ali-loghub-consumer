package com.github.charleslzq.loghub.converter;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public enum LogGroupHeaders {
    SOURCE("source"),
    TOPIC("topic"),
    MACHINE_UUID("machine_uuid"),
    CATEGORY("category");

    private final String key;

    LogGroupHeaders(String key) {
        this.key = this.getClass().getName() + "." + key;
    }

    public String getKey() {
        return key;
    }
}
