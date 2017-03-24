package com.marc.microservice.commander.listener.ordercart;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemWS {
	String uuid;
	String itemUuid;
	Integer quantity;
	BigDecimal unitPrice;
}
