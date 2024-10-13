package com.tweets.util;

public class SystemContext {
	private static ThreadLocal<String> parentCorrelationId;
	private static ThreadLocal<String> childCorrelationId;
	
	static {
		parentCorrelationId = new ThreadLocal<>();
		childCorrelationId = new ThreadLocal<>();
	}
	
	public static void setParentCorrelationId(String correlationId) {
		parentCorrelationId.set(correlationId);
	}
	
	public static void setChildCorrelationId(String correlationId) {
		childCorrelationId.set(correlationId);
	}
	
	public static String getParentCorrelationId() {
		return parentCorrelationId.get();
	}
	
	public static String getChildCorrelationId() {
		return childCorrelationId.get();
	}
}
