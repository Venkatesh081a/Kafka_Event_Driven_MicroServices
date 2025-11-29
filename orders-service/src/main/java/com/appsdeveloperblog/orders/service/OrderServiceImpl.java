package com.appsdeveloperblog.orders.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.appsdeveloper.commons.OrderCreatedEvent;
import com.appsdeveloper.commons.dto.Order;
import com.appsdeveloper.commons.dto.OrderStatus;
import com.appsdeveloperblog.orders.dao.jpa.entity.OrderEntity;
import com.appsdeveloperblog.orders.dao.jpa.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${orders.events.topic.name}")
	private String ordersEventsTopicName;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Override
	public Order placeOrder(Order order) {
		LOGGER.info("Received new oder request for product : " + order.getProductId());
		OrderEntity entity = new OrderEntity();
		entity.setCustomerId(order.getCustomerId());
		entity.setProductId(order.getProductId());
		entity.setProductQuantity(order.getProductQuantity());
		entity.setStatus(OrderStatus.CREATED);
		orderRepository.save(entity);

		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(entity.getId(), entity.getCustomerId(),
				entity.getProductId(), entity.getProductQuantity());

		kafkaTemplate.send(ordersEventsTopicName, orderCreatedEvent);
		LOGGER.info("Order Placed Request send to topic : " + ordersEventsTopicName);

		return new Order(entity.getId(), entity.getCustomerId(), entity.getProductId(), entity.getProductQuantity(),
				entity.getStatus());
	}

	@Override
	public void approveOrder(UUID orderId) {
		OrderEntity existingOrder = orderRepository.findById(orderId).orElse(null);
		Assert.notNull(existingOrder, "No order is found with id " + orderId + " in the DB");
		existingOrder.setStatus(OrderStatus.APPROVED);
		orderRepository.save(existingOrder);
	}

	@Override
	public void rejectOrder(UUID orderId) {
		OrderEntity existingOrder = orderRepository.findById(orderId).orElse(null);
		Assert.notNull(existingOrder, "No order is found with id " + orderId + " in the DB");
		existingOrder.setStatus(OrderStatus.REJECTED);
		orderRepository.save(existingOrder);
	}

}
