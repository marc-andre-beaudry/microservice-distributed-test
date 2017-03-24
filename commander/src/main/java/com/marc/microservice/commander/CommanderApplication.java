package com.marc.microservice.commander;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CommanderApplication {



	public static void main(String[] args) {
		SpringApplication.run(CommanderApplication.class, args);
	}
}
