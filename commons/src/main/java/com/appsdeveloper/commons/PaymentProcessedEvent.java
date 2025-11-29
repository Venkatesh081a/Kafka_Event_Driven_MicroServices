package com.appsdeveloper.commons;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentProcessedEvent {

	private UUID paymentId;

	private UUID orderId;

	public PaymentProcessedEvent(UUID paymentId, UUID orderId) {
		this.paymentId = paymentId;
		this.orderId = orderId;

	}

	public PaymentProcessedEvent() {
	}

	public UUID getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(UUID paymentId) {
		this.paymentId = paymentId;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

}
