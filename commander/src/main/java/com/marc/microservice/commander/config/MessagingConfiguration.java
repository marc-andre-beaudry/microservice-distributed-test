package com.marc.microservice.commander.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

	@Value("${topic.exchange.name}")
	private String topicExchangeName;

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(topicExchangeName);
	}
}
