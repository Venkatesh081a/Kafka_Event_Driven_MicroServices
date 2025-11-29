package com.appsdeveloperblog.orders.service;

import java.util.UUID;

import com.appsdeveloper.commons.dto.Order;

public interface OrderService {
	
	Order placeOrder(Order order);

	void approveOrder(UUID orderId);

	void rejectOrder(UUID orderId);
}
