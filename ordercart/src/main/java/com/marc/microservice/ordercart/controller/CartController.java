package com.marc.microservice.ordercart.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marc.microservice.ordercart.entity.Item;
import com.marc.microservice.ordercart.entity.OrderCart;
import com.marc.microservice.ordercart.entity.OrderItem;
import com.marc.microservice.ordercart.entity.OrderStatus;
import com.marc.microservice.ordercart.repository.ItemRepository;
import com.marc.microservice.ordercart.repository.OrderCartRepository;
import com.marc.microservice.ordercart.ws.OrderCartWS;
import com.marc.microservice.ordercart.ws.OrderItemWS;

@Slf4j
@RestController
@RequestMapping("/api")
public class CartController {

	@Value("${cart.event.queue.name}")
	private String cartEventQueueName;

	private final ObjectMapper mapper = new ObjectMapper();
	private final ItemRepository itemRepository;
	private final OrderCartRepository orderCartRepository;
	private final RabbitTemplate rabbitTemplate;

	@Autowired
	public CartController(ItemRepository itemRepository, OrderCartRepository orderCartRepository, RabbitTemplate rabbitTemplate) {
		this.itemRepository = itemRepository;
		this.orderCartRepository = orderCartRepository;
		this.rabbitTemplate = rabbitTemplate;
	}

	@RequestMapping(value = "/carts", method = RequestMethod.GET)
	public List<OrderCartWS> getCarts() {
		return orderCartRepository.findAll().stream().map(this::convertToWS).collect(Collectors.toList());
	}

	@RequestMapping(value = "/carts/{cartUuid}", method = RequestMethod.GET)
	public OrderCartWS getCart(@PathVariable(name = "cartUuid") String cartUuid) {
		return convertToWS(orderCartRepository.findByUuid(cartUuid));
	}

	@RequestMapping(value = "/carts", method = RequestMethod.POST)
	public OrderCartWS createCart(@RequestBody OrderCartWS orderCartWs) {
		log.info("POST /carts");
		OrderCart orderCart = new OrderCart();
		for (OrderItemWS itemWS : orderCartWs.getItems()) {
			Item item = itemRepository.findByUuid(itemWS.getItemUuid());
			OrderItem orderItem = new OrderItem().builder()
					.orderCart(orderCart)
					.uuid(UUID.randomUUID().toString())
					.item(item)
					.quantity(itemWS.getQuantity())
					.build();
			orderCart.getItems().add(orderItem);
		}
		orderCart = orderCartRepository.save(orderCart);
		return convertToWS(orderCart);
	}

	@RequestMapping(value = "/carts/{cartUuid}/checkout", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public OrderCartWS checkout(@PathVariable(name = "cartUuid") String cartUuid) {
		log.info("POST /carts/{uuid}/checkout");

		OrderCart orderCart = orderCartRepository.findByUuid(cartUuid);
		orderCart.setStatus(OrderStatus.PENDING);
		orderCart = orderCartRepository.save(orderCart);

		OrderCartWS orderCartWS = convertToWS(orderCart);
		try {
			rabbitTemplate.convertAndSend(cartEventQueueName, mapper.writeValueAsString(orderCartWS));
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return orderCartWS;
	}

	@RequestMapping(value = "/carts/{cartUuid}/complete", method = RequestMethod.POST)
	public OrderCartWS complete(@PathVariable(name = "cartUuid") String cartUuid) {
		log.info("POST /carts/{uuid}/complete");

		OrderCart orderCart = orderCartRepository.findByUuid(cartUuid);
		orderCart.setStatus(OrderStatus.COMPLETE);
		return convertToWS(orderCartRepository.save(orderCart));
	}

	@RequestMapping(value = "/carts/{cartUuid}/failed", method = RequestMethod.POST)
	public OrderCartWS failed(@PathVariable(name = "cartUuid") String cartUuid) {
		log.info("POST /carts/{uuid}/failed");

		OrderCart orderCart = orderCartRepository.findByUuid(cartUuid);
		orderCart.setStatus(OrderStatus.FAILED);
		return convertToWS(orderCartRepository.save(orderCart));
	}

	private OrderCartWS convertToWS(OrderCart orderCart) {
		OrderCartWS orderCartWS = new OrderCartWS();
		orderCartWS.setUuid(orderCart.getUuid());
		orderCartWS.setStatus(orderCart.getStatus());
		for (OrderItem orderItem : orderCart.getItems()) {
			OrderItemWS orderItemWS = new OrderItemWS();
			orderItemWS.setUuid(orderItem.getUuid());
			orderItemWS.setItemUuid(orderItem.getItem().getUuid());
			orderItemWS.setUnitPrice(orderItem.getItem().getUnitPrice());
			orderItemWS.setQuantity(orderItem.getQuantity());
			orderCartWS.getItems().add(orderItemWS);
		}
		return orderCartWS;
	}
}
