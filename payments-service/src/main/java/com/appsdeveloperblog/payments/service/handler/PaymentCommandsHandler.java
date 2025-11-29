package com.appsdeveloperblog.payments.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.appsdeveloper.commons.PaymentProcessedEvent;
import com.appsdeveloper.commons.PaymentProcessingFailedEvent;
import com.appsdeveloper.commons.commands.ProcessPaymentCommand;
import com.appsdeveloper.commons.dto.Payment;
import com.appsdeveloper.commons.error.CreditCardProcessorUnavailableException;
import com.appsdeveloperblog.payments.service.PaymentService;

@KafkaListener(topics = { "${payments.commands.topic.name}" })
@Component
public class PaymentCommandsHandler {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private PaymentService paymentService;

	@Value("${payments.events.topic.name}")
	private String paymentsEventsTopicName;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@KafkaHandler
	public void handleCommand(@Payload ProcessPaymentCommand processPaymentCommand) {
		try {
			LOGGER.info("Received ProcessPaymentCommand Request for product : " + processPaymentCommand.getProductId());

			Payment payment = new Payment();
			payment.setOrderId(processPaymentCommand.getOrderId());
			payment.setProductId(processPaymentCommand.getProductId());
			payment.setProductPrice(processPaymentCommand.getProductPrice());
			payment.setProductQuantity(processPaymentCommand.getProductQuantity());

			Payment procesedPayment = paymentService.process(payment);

			PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(procesedPayment.getId(),
					procesedPayment.getOrderId());

			kafkaTemplate.send(paymentsEventsTopicName, paymentProcessedEvent);

		} catch (CreditCardProcessorUnavailableException e) {
			LOGGER.error(e.getMessage(), e);
			PaymentProcessingFailedEvent paymentFailedEvent = new PaymentProcessingFailedEvent(
					processPaymentCommand.getOrderId(), processPaymentCommand.getProductId(),
					processPaymentCommand.getProductQuantity());

			kafkaTemplate.send(paymentsEventsTopicName, paymentFailedEvent);

		}

	}
}
