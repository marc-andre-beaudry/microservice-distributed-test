package com.marc.microservice.commander.listener.provisioning;

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

@Component
@Slf4j
public class ProvisioningEventListener implements MessageListener, MessageRecoverer {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${cart-service.api-url}")
	private String cartServiceApiUrl;

	@Override
	public void onMessage(Message message) {
		log.info("ProvisioningEventListener : " + message);
		ProvisionBean bean;
		try {
			bean = objectMapper.readValue(message.getBody(), ProvisionBean.class);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new AmqpRejectAndDontRequeueException(e);
		}
		String url = cartServiceApiUrl + "/" + bean.getCartUuid()  + "/complete";
		ResponseEntity<?> responseEntity = restTemplate.postForEntity(url, null, String.class);
		log.info("Status Code " + responseEntity.getStatusCodeValue());
	}

	@Override
	public void recover(Message message, Throwable throwable) {
		throw new AmqpRejectAndDontRequeueException("Unable to recover");
	}
}
