package com.appsdeveloperblog.orders.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloper.commons.OrderApprovedEvent;
import com.appsdeveloper.commons.commands.ApproveOrderCommand;
import com.appsdeveloper.commons.commands.RejectOrderCommand;
import com.appsdeveloper.commons.dto.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import com.appsdeveloperblog.orders.service.OrderService;

@Component
@KafkaListener(topics = { "${orders.commands.topic.name}" })
public class OrderCommandsHandler {

	@Value("${orders.events.topic.name}")
	private String ordersEventsTopicName;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderHistoryService orderHistoryService;

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@KafkaHandler
	public void handleCommand(@Payload ApproveOrderCommand approveOrderCommand) {
		LOGGER.info("Received ApproveOrderCommand Request for order : " + approveOrderCommand.getOrderId());
		orderService.approveOrder(approveOrderCommand.getOrderId());
		OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent();
		orderApprovedEvent.setOrderId(approveOrderCommand.getOrderId());

		kafkaTemplate.send(ordersEventsTopicName, orderApprovedEvent);
	}

	@KafkaHandler
	public void handleCommand(@Payload RejectOrderCommand rejectOrderCommand) {
		LOGGER.info("Received RejectOrderCommand Request for order : " + rejectOrderCommand.getOrderId());
		orderHistoryService.add(rejectOrderCommand.getOrderId(), OrderStatus.REJECTED);
	}
}
