package com.appsdeveloperblog.orders.service;

import com.appsdeveloper.commons.dto.OrderStatus;
import com.appsdeveloperblog.orders.dto.OrderHistory;

import java.util.List;
import java.util.UUID;

public interface OrderHistoryService {
	void add(UUID orderId, OrderStatus orderStatus);

	List<OrderHistory> findByOrderId(UUID orderId);
}
