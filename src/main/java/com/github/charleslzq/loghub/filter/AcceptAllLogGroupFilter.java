package com.github.charleslzq.loghub.filter;

import com.aliyun.openservices.log.common.LogGroupData;

/**
 * Created by Charles on 3/3/2017.
 */
public class AcceptAllLogGroupFilter implements LogGroupFilter {
	@Override
	public boolean accept(LogGroupData logGroupData) {
		return true;
	}
}
