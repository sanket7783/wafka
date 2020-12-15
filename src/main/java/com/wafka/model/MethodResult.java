package com.wafka.model;

public class MethodResult {
	private final String methodName;
	private final Object result;

	public MethodResult(String methodName, Object result) {
		this.methodName = methodName;
		this.result = result;
	}

	public String getMethodName() {
		return methodName;
	}

	public Object getResult() {
		return result;
	}
}
