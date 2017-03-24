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

import com.marc.microservice.commander.listener.payment.PaymentEventListener;

@Configuration
public class PaymentEventConfiguration {

	@Value("${topic.exchange.name}")
	private String topicExchangeName;
	@Value("${payment.event.queue.name}")
	private String paymentEventQueueName;

	@Bean(name = "paymentEventQueue")
	public Queue paymentEventQueue() {
		return new Queue(paymentEventQueueName, true);
	}

	@Bean
	Binding bindingPaymentEvent(Queue paymentEventQueue, TopicExchange exchange) {
		return BindingBuilder.bind(paymentEventQueue).to(exchange).with(paymentEventQueueName);
	}

	@Bean
	SimpleMessageListenerContainer paymentEventListenerContainer(ConnectionFactory connectionFactory, PaymentEventListener paymentEventListener) {
		SimpleMessageListenerContainer paymentEventListenerContainer = new SimpleMessageListenerContainer();
		paymentEventListenerContainer.setConnectionFactory(connectionFactory);
		paymentEventListenerContainer.setQueueNames(paymentEventQueueName);
		paymentEventListenerContainer.setMessageListener(new MessageListenerAdapter(paymentEventListener, "receiveEvent"));
		paymentEventListenerContainer.setChannelTransacted(true);
		paymentEventListenerContainer.setAdviceChain(
				RetryInterceptorBuilder.stateful()
						.maxAttempts(2)
						.backOffOptions(1000, 2, 10000)
						.build()
		);
		return paymentEventListenerContainer;
	}
}
