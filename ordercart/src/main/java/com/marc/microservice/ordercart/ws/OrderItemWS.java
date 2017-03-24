package com.marc.microservice.ordercart.ws;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemWS {
	String uuid;
	String itemUuid;
	Integer quantity;
	BigDecimal unitPrice;
}
