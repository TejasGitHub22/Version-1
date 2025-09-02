package com.coffee.coffeeApp.service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import com.coffee.coffeeApp.dto.CoffeeMachineDataDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;

@Service
public class MQTTSubscriberService {

	private final CoffeeMachineService coffeeMachineService;
	private final ExecutorService executorService = Executors.newFixedThreadPool(50);
	private final ObjectMapper objectMapper;

	public MQTTSubscriberService(CoffeeMachineService coffeeMachineService, ObjectMapper objectMapper) {
		this.coffeeMachineService = coffeeMachineService;
		this.objectMapper = objectMapper;
	}

	@Value("${mqtt.broker.url}")
	private String broker;

	@Value("${mqtt.username}")
	private String username;

	@Value("${mqtt.password}")
	private String password;

	private IMqttClient client;

	public void start() throws MqttException {
		String clientId = "backend-subscriber-" + UUID.randomUUID();
		MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(username);
		options.setPassword(password.toCharArray());
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);

		// connecting to the broker...
		client.connect(options);

		// subscribing to all coffee machine topics...
		client.subscribe("coffeemachine/+/data", (topic, msg) -> {
			executorService.submit(() -> {
				try {
					String payload = new String(msg.getPayload());
					CoffeeMachineDataDto dto = objectMapper.readValue(payload, CoffeeMachineDataDto.class);
					coffeeMachineService.updateMachineData(dto);
					System.out.println(payload);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		
		System.out.println("MQTT Subscriber listening on topic coffeemachine/+/data");
	}
	
	@PreDestroy
	public void shutdown() throws MqttException{
		if(client != null && client.isConnected()) {
			client.disconnect();
			client.close();
		}
		executorService.shutdown();
	}
}