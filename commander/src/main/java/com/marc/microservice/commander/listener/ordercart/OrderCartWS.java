package com.marc.microservice.commander.listener.ordercart;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class OrderCartWS {
	String uuid;
	String status;
	List<OrderItemWS> items = new ArrayList<>();
}
