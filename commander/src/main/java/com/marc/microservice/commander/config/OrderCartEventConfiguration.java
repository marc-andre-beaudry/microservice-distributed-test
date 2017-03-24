package com.marc.microservice.commander.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marc.microservice.commander.listener.ordercart.OrderEventListener;

@Configuration
public class OrderCartEventConfiguration {

	@Value("${topic.exchange.name}")
	private String topicExchangeName;
	@Value("${cart.event.queue.name}")
	private String cartEventQueueName;

	@Bean(name = "cartEventQueue")
	public Queue cartEventQueue() {
		return new Queue(cartEventQueueName, true);
	}

	@Bean
	public Binding bindingCartEvent(Queue cartEventQueue, TopicExchange topicExchange) {
		return BindingBuilder.bind(cartEventQueue).to(topicExchange).with(cartEventQueueName);
	}

	@Bean
	public SimpleMessageListenerContainer orderEventListenerContainer(ConnectionFactory connectionFactory, OrderEventListener orderEventListener) {
		SimpleMessageListenerContainer orderEventListenerContainer = new SimpleMessageListenerContainer();
		orderEventListenerContainer.setConnectionFactory(connectionFactory);
		orderEventListenerContainer.setQueueNames(cartEventQueueName);
		orderEventListenerContainer.setMessageListener(new MessageListenerAdapter(orderEventListener, "receiveEvent"));
		orderEventListenerContainer.setChannelTransacted(true);
		orderEventListenerContainer.setAdviceChain(
				RetryInterceptorBuilder.stateful()
						.maxAttempts(2)
						.backOffOptions(1000, 2, 10000)
						.build()
		);
		return orderEventListenerContainer;
	}
}
