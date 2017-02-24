package com.github.charleslzq.loghub.listener;

import org.springframework.messaging.Message;

import java.util.Map;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public interface MessageListener {
    void onMessage(Message<Map<String, String>> message);

    default String name() {
        return this.getClass().getName();
    }
}
