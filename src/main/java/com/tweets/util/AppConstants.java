package com.tweets.util;

import lombok.Getter;

@Getter
public enum AppConstants {
	PARENT_CORRELATION_ID_HEADER("X-Parent-Correlation-ID"),
	CHILD_CORRELATION_ID_HEADER("X-Child-Correlation-ID"),
	MDC_PARENT_CORRELATION_ID_KEY("parentCorrelationId"),
	MDC_CHILD_CORRELATION_ID_KEY("childCorrelationId");
	
	private String value;
	
	private AppConstants(String value) {
		this.value = value;
	}

}