package com.appsdeveloper.commons.error;

import java.util.UUID;

public class ProductInsufficientQuantityException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final UUID productId;
	private final UUID orderId;

	public ProductInsufficientQuantityException(UUID productId, UUID orderId) {
		super("Product " + productId + " has insufficient quantity in the stock for order " + orderId);
		this.productId = productId;
		this.orderId = orderId;
	}

	public UUID getProductId() {
		return productId;
	}

	public UUID getOrderId() {
		return orderId;
	}
}
