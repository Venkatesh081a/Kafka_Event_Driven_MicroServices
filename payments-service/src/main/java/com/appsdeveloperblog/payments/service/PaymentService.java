package com.appsdeveloperblog.payments.service;

import java.util.List;

import com.appsdeveloper.commons.dto.Payment;

public interface PaymentService {
	List<Payment> findAll();

	Payment process(Payment payment);
}
