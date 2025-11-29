package com.appsdeveloper.commons.error;

public class RetryableException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RetryableException(Throwable cause) {
		super(cause);
	}

	public RetryableException(String message) {
		super(message);
	}
}
