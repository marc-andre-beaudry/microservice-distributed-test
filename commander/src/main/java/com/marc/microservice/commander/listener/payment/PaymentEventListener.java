package com.marc.microservice.commander.listener.payment;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marc.microservice.commander.listener.provisioning.ProvisionBean;

@Component
@Slf4j
public class PaymentEventListener implements MessageListener, MessageRecoverer {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${provisioning-service.api-url}")
	private String provisioningServiceApiUrl;

	@Override
	public void onMessage(Message message) {

		log.info("PaymentEventListener : " + message);
		PaymentBean bean;
		try {
			bean = objectMapper.readValue(message.getBody(), PaymentBean.class);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new AmqpRejectAndDontRequeueException(e);
		}

		ProvisionBean provisionBean = new ProvisionBean();
		provisionBean.setCartUuid(bean.getCartUuid());
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(provisioningServiceApiUrl, provisionBean, String.class);
		log.info("Status Code " + responseEntity.getStatusCodeValue());
	}

	@Override
	public void recover(Message message, Throwable throwable) {
		throw new AmqpRejectAndDontRequeueException("Unable to recover");
	}
}
