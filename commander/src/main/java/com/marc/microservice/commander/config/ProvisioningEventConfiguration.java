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

import com.marc.microservice.commander.listener.provisioning.ProvisioningEventListener;

@Configuration
public class ProvisioningEventConfiguration {

	@Value("${topic.exchange.name}")
	private String topicExchangeName;
	@Value("${provisioning.event.queue.name}")
	private String provisioningEventQueueName;

	@Bean(name = "provisioningEventQueue")
	public Queue provisioningEventQueue() {
		return new Queue(provisioningEventQueueName, true);
	}

	@Bean
	Binding bindingProvisioningEvent(Queue provisioningEventQueue, TopicExchange exchange) {
		return BindingBuilder.bind(provisioningEventQueue).to(exchange).with(provisioningEventQueueName);
	}

	@Bean
	SimpleMessageListenerContainer provisioningEventListenerContainer(ConnectionFactory connectionFactory,
																	  ProvisioningEventListener provisioningEventListener) {
		SimpleMessageListenerContainer provisioningEventListenerContainer = new SimpleMessageListenerContainer();
		provisioningEventListenerContainer.setConnectionFactory(connectionFactory);
		provisioningEventListenerContainer.setQueueNames(provisioningEventQueueName);
		provisioningEventListenerContainer.setMessageListener(new MessageListenerAdapter(provisioningEventListener, "receiveEvent"));
		provisioningEventListenerContainer.setChannelTransacted(true);
		provisioningEventListenerContainer.setAdviceChain(
				RetryInterceptorBuilder.stateful()
						.maxAttempts(2)
						.backOffOptions(1000, 2, 10000)
						.build()
		);
		return provisioningEventListenerContainer;
	}
}
