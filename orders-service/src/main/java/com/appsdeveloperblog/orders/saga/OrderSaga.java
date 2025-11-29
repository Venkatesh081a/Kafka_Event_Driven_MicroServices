package com.appsdeveloperblog.orders.saga;

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
import com.appsdeveloper.commons.OrderCreatedEvent;
import com.appsdeveloper.commons.PaymentProcessedEvent;
import com.appsdeveloper.commons.PaymentProcessingFailedEvent;
import com.appsdeveloper.commons.ProductReservationCancelledEvent;
import com.appsdeveloper.commons.ProductReservedEvent;
import com.appsdeveloper.commons.commands.ApproveOrderCommand;
import com.appsdeveloper.commons.commands.CancelProductReservationCommand;
import com.appsdeveloper.commons.commands.ProcessPaymentCommand;
import com.appsdeveloper.commons.commands.RejectOrderCommand;
import com.appsdeveloper.commons.commands.ReserveProductCommand;
import com.appsdeveloper.commons.dto.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;

@Component
@KafkaListener(topics = { "${orders.events.topic.name}", "${products.events.topic.name}",
		"${payments.events.topic.name}" })
public class OrderSaga {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${products.commands.topic.name}")
	private String productsCommandsTopicName;

	@Value("${payments.commands.topic.name}")
	private String paymentsCommandsTopicName;

	@Value("${orders.commands.topic.name}")
	private String ordersCommandsTopicName;

	@Autowired
	private OrderHistoryService orderHistoryService;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@KafkaHandler
	public void handleEvent(@Payload OrderCreatedEvent orderCreatedEvent) {
		LOGGER.info("Received OrderCreateEvent Request  for order : " + orderCreatedEvent.getOrderId());
		ReserveProductCommand reserveProductCommand = new ReserveProductCommand();
		reserveProductCommand.setProductId(orderCreatedEvent.getProductId());
		reserveProductCommand.setProductQuantity(orderCreatedEvent.getProductQuantity());
		reserveProductCommand.setOrderId(orderCreatedEvent.getOrderId());
		
		kafkaTemplate.send(productsCommandsTopicName, reserveProductCommand);
		orderHistoryService.add(orderCreatedEvent.getOrderId(), OrderStatus.CREATED);
	}

	@KafkaHandler
	public void handleEvent(@Payload ProductReservedEvent productReservedEvent) {
		LOGGER.info("Received ProductReservedEvent Request for product : " + productReservedEvent.getProductId());
		ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(productReservedEvent.getOrderId(),
				productReservedEvent.getProductId(), productReservedEvent.getProductPrice(),
				productReservedEvent.getProductQuantity());

		kafkaTemplate.send(paymentsCommandsTopicName, processPaymentCommand);

	}

	@KafkaHandler
	public void handleEvent(@Payload PaymentProcessedEvent paymentProcessedEvent) {
		LOGGER.info("Received PaymentProcessedEvent Request for product : " + paymentProcessedEvent.getOrderId());
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand();
		approveOrderCommand.setOrderId(paymentProcessedEvent.getOrderId());
		kafkaTemplate.send(ordersCommandsTopicName, approveOrderCommand);
	}

	@KafkaHandler
	public void handleEvent(@Payload OrderApprovedEvent orderApprovedEvent) {
		LOGGER.info("Received OrderApprovedEvent Request for order : " + orderApprovedEvent.getOrderId());
		orderHistoryService.add(orderApprovedEvent.getOrderId(), OrderStatus.APPROVED);
	}

	@KafkaHandler
	public void handleEvent(@Payload PaymentProcessingFailedEvent failedEvent) {
		LOGGER.info("Received PaymentProcessingFailedEvent for order : " + failedEvent.getOrderId());
		CancelProductReservationCommand cancelProductReservationCommand = new CancelProductReservationCommand(
				failedEvent.getProductId(), failedEvent.getProductQuantity(), failedEvent.getOrderId());
		kafkaTemplate.send(productsCommandsTopicName, cancelProductReservationCommand);
	}

	@KafkaHandler
	public void handleEvent(@Payload ProductReservationCancelledEvent event) {
		LOGGER.info("Received ProductReservationCancelledEvent for order : " + event.getOrderId());
		RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId());
		kafkaTemplate.send(ordersCommandsTopicName, rejectOrderCommand);
	}

}
