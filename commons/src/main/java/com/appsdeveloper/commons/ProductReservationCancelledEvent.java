package com.appsdeveloper.commons;

import java.util.UUID;

public class ProductReservationCancelledEvent {

	private UUID productId;
	
	private UUID orderId;

	public ProductReservationCancelledEvent(UUID productId, UUID orderId) {
		this.productId = productId;
		this.orderId = orderId;
	}

	public ProductReservationCancelledEvent() {
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

}
