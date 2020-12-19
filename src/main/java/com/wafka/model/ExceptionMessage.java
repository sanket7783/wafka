package com.wafka.model;

public class ExceptionMessage {
	private String message;
	private int code;
	private String httpStatus;

	public ExceptionMessage(String message, int code, String httpStatus) {
		this.message = message;
		this.code = code;
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	@Override
	public String toString() {
		return "ExceptionMessage(message=" + message + ", code=" + code + ", status=" + httpStatus + ")";
	}
}
