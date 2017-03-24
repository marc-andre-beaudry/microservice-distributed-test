package com.marc.microservice.commander.listener.ordercart;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marc.microservice.commander.listener.payment.PaymentBean;

@Component
@Slf4j
public class OrderEventListener implements MessageListener, MessageRecoverer {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${payment-service.api-url}")
	private String paymentServiceApiUrl;

	@Value("${cart-service.api-url}")
	private String cartServiceApiUrl;

	@Override
	@Transactional
	public void onMessage(Message message) {
		log.info("OrderEventListener : " + message);
		OrderCartWS orderCartWS = convertToCart(message).orElseThrow(() -> new AmqpRejectAndDontRequeueException("Invalid parsing exception"));
		BigDecimal amount = getOrderCartTotal(orderCartWS);
		PaymentBean paymentBean = new PaymentBean();
		paymentBean.setCartUuid(orderCartWS.getUuid());
		paymentBean.setAmount(amount);
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(paymentServiceApiUrl, paymentBean, String.class);
		log.info("Status Code " + responseEntity.getStatusCodeValue());
	}

	@Override
	public void recover(Message message, Throwable throwable) {
		OrderCartWS orderCartWS = convertToCart(message).orElseThrow(() -> new AmqpRejectAndDontRequeueException("Invalid parsing exception"));
		String url = cartServiceApiUrl + "/" + orderCartWS.getUuid()  + "/failed";
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(url, null, String.class);
		log.info("Status Code " + responseEntity.getStatusCodeValue());
	}

	private Optional<OrderCartWS> convertToCart(Message message) {
		OrderCartWS orderCartWS = null;
		try {
			orderCartWS = objectMapper.readValue(message.getBody(), OrderCartWS.class);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return Optional.ofNullable(orderCartWS);
	}

	private BigDecimal getOrderCartTotal(OrderCartWS orderCartWS) {
		return orderCartWS.getItems().stream().map(x -> x.getUnitPrice().multiply(BigDecimal.valueOf(x.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
