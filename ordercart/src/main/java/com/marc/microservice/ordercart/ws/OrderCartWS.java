package com.marc.microservice.ordercart.ws;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.marc.microservice.ordercart.entity.OrderStatus;

@Data
public class OrderCartWS {
	String uuid;
	OrderStatus status;
	List<OrderItemWS> items = new ArrayList<>();
}
