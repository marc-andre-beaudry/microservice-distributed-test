package com.marc.microservice.ordercart.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;

@Configuration
public class MessagingConfiguration {

	@Value("${topic.exchange.name}")
	private String topicExchangeName;

	@Value("${cart.event.queue.name}")
	private String cartEventQueueName;

	@Bean
	public Queue queue() {
		return new Queue(cartEventQueueName, true);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(topicExchangeName);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(cartEventQueueName);
	}

	@Bean
	public StatefulRetryOperationsInterceptor retryOperationsInterceptor() {
		return RetryInterceptorBuilder.stateful()
				.maxAttempts(5)
				.backOffOptions(5000, 1.0, 10000)
				.build();
	}

	@Bean
	public SimpleMessageConverter simpleMessageConverter() {
		SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
		simpleMessageConverter.setCreateMessageIds(true);
		return simpleMessageConverter;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setConnectionFactory(connectionFactory);
		rabbitTemplate.setChannelTransacted(true);
		rabbitTemplate.setMessageConverter(simpleMessageConverter());
		return rabbitTemplate;
	}
}
