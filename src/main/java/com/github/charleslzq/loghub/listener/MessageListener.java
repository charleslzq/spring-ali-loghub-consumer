package com.github.charleslzq.loghub.listener;

import com.github.charleslzq.loghub.converter.LogData;
import org.springframework.messaging.Message;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public interface MessageListener {
    void onMessage(Message<LogData> message);

    default String name() {
        return this.getClass().getName();
    }
}
