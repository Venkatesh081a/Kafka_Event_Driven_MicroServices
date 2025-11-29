package com.appsdeveloper.commons;

import java.util.UUID;

public class OrderApprovedEvent {

	private UUID orderId;

	public OrderApprovedEvent(UUID orderId) {
		this.orderId = orderId;
	}

	public OrderApprovedEvent() {
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

}
