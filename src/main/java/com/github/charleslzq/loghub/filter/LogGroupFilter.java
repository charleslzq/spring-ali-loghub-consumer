package com.github.charleslzq.loghub.filter;

import com.aliyun.openservices.log.common.LogGroupData;

/**
 * Created by Charles on 3/3/2017.
 */
public interface LogGroupFilter {
	boolean accept(LogGroupData logGroupData);
}
