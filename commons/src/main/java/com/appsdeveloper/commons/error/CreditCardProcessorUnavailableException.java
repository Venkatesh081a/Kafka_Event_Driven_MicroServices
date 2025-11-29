package com.appsdeveloper.commons.error;

public class CreditCardProcessorUnavailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CreditCardProcessorUnavailableException(Throwable cause) {
		super(cause);
	}
}
