package com.github.charleslzq.loghub.filter;

import com.aliyun.openservices.log.common.Logs;

/**
 * Created by Charles on 3/3/2017.
 */
public class AcceptAllLogFilter implements LogFilter {
	@Override
	public boolean accept(Logs.Log log) {
		return true;
	}
}
