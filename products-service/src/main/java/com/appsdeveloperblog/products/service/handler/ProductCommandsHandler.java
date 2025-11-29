package com.appsdeveloperblog.products.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloper.commons.ProductReservationCancelledEvent;
import com.appsdeveloper.commons.ProductReservationFailedEvent;
import com.appsdeveloper.commons.ProductReservedEvent;
import com.appsdeveloper.commons.commands.CancelProductReservationCommand;
import com.appsdeveloper.commons.commands.ReserveProductCommand;
import com.appsdeveloper.commons.dto.Product;
import com.appsdeveloperblog.products.service.ProductService;

@Component
@KafkaListener(topics = { "${products.commands.topic.name}" })
public class ProductCommandsHandler {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private ProductService productService;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Value("${products.events.topic.name}")
	private String productsEventsTopicName;

	@KafkaHandler
	public void handleCommand(@Payload ReserveProductCommand command) {
		try {
			LOGGER.info("Received ReserveProductCommand Request for product : " + command.getProductId());
			Product desiredProduct = new Product(command.getProductId(), command.getProductQuantity());
			Product reservedProduct = productService.reserve(desiredProduct, command.getOrderId());
			ProductReservedEvent productReservedEvent = new ProductReservedEvent(command.getOrderId(),
					command.getProductId(), reservedProduct.getPrice(), command.getProductQuantity());

			kafkaTemplate.send(productsEventsTopicName, productReservedEvent);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			ProductReservationFailedEvent reservationFailedEvent = new ProductReservationFailedEvent(
					command.getOrderId(), command.getProductId(), command.getProductQuantity());
			kafkaTemplate.send(productsEventsTopicName, reservationFailedEvent);

		}

	}

	@KafkaHandler
	public void handleCommand(@Payload CancelProductReservationCommand command) {
		LOGGER.info("Received CancelProductReservationCommand for product : " + command.getProductId());
		Product product = new Product(command.getProductId(), command.getProductQuantity());
		productService.cancelReservation(product, command.getOrderId());

		ProductReservationCancelledEvent productReservationCancelledEvent = new ProductReservationCancelledEvent(
				command.getProductId(), command.getOrderId());
		kafkaTemplate.send(productsEventsTopicName, productReservationCancelledEvent);
	}
}
