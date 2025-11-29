package com.appsdeveloper.commons.error;

public class NotRetryableException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public NotRetryableException(Exception exception) {
        super(exception);
    }

    public NotRetryableException(String message) {
        super(message);
    }
}
