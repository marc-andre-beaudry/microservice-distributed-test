package com.marc.microservice.commander.listener.payment;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentBean {
	private String cartUuid;
	private BigDecimal amount;
}
