package com.github.charleslzq.loghub.listener;

import java.util.List;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public interface ListenerRegistry {
    List<MessageListener> findListener(String groupName);
}
