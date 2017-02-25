package com.github.charleslzq.loghub.listener;

import org.springframework.messaging.Message;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public interface MessageListener<T> {
    void onMessage(Message<T> message);
}
