package com.appsdeveloper.commons.commands;

import java.util.UUID;

public class ApproveOrderCommand {

	private UUID orderId;

	public ApproveOrderCommand(UUID orderId) {
		this.orderId = orderId;
	}

	public ApproveOrderCommand() {
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

}
