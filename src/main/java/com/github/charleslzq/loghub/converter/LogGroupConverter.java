package com.github.charleslzq.loghub.converter;

import com.aliyun.openservices.log.common.LogGroupData;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Map;

/**
 * Created by liuzhengqi on 2/24/2017.
 */
public interface LogGroupConverter {
    List<Message<Map<String, String>>> convert(LogGroupData logGroupData);
}
