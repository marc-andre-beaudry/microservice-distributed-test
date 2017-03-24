package com.marc.microservice.payment.ws;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

import com.marc.microservice.payment.entity.Payment;

@Getter
@Setter
public class PaymentWS {

	private String uuid;
	private String cartUuid;
	private BigDecimal amount;

	public static PaymentWS fromEntity(Payment payment) {
		PaymentWS bean = new PaymentWS();
		bean.setUuid(payment.getUuid());
		bean.setCartUuid(payment.getCartUuid());
		bean.setAmount(payment.getAmount());
		return bean;
	}
}
