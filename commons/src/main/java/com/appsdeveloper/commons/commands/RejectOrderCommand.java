package com.appsdeveloper.commons.commands;

import java.util.UUID;

public class RejectOrderCommand {

	private UUID orderId;

	public RejectOrderCommand(UUID orderId) {
		this.orderId = orderId;
	}

	public RejectOrderCommand() {
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

}
