package com.coffee.coffeeApp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.coffee.coffeeApp.service.MQTTSubscriberService;

@Configuration
public class MqttSubscriberConfig {
	
	@Bean
	CommandLineRunner startMqttSubscriber(MQTTSubscriberService subscriberService) {
		return args -> {
			try {
				subscriberService.start();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		};
	}
}
