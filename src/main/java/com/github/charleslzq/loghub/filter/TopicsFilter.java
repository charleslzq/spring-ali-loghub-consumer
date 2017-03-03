package com.github.charleslzq.loghub.filter;

import com.aliyun.openservices.log.common.LogGroupData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Charles on 3/3/2017.
 */
public class TopicsFilter implements LogGroupFilter {
	private final Set<String> topics = new HashSet<>();

	public TopicsFilter(String[] topics) {
		this.topics.addAll(Arrays.asList(topics));
	}

	@Override
	public boolean accept(LogGroupData logGroupData) {
		return topics.contains(logGroupData.GetLogGroup().getTopic());
	}
}
