package com.coffeemachine.simulator.service;

import com.coffeemachine.simulator.model.MachineData;
import com.coffeemachine.simulator.repository.MachineDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MachineSimulatorService {

    private final ObjectMapper objectMapper;
    private final MachineDataRepository machineDataRepository;
    private MqttClient mqttClient;

    // MQTT Config from application.properties
    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    private String clientId = "mqtt-simulator-client" + UUID.randomUUID();

    @Value("${mqtt.username}")
    private String username = "your-hivemq-username";

    @Value("${mqtt.password}")
    private String password = "your-hivemq-password";

    public MachineSimulatorService(ObjectMapper objectMapper, MachineDataRepository machineDataRepository) {
        this.objectMapper = objectMapper;
        this.machineDataRepository = machineDataRepository;
    }

    @PostConstruct
    public void init() {
        // Initialize MQTT client after properties are injected
        initializeMqttClient();
    }

    private void initializeMqttClient() {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);

            // Set up connection options with authentication
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setCleanSession(true);

            mqttClient.connect(options);
            System.out.println("✅ MQTT Client connected to: " + brokerUrl);
        } catch (MqttException e) {
            System.err.println("❌ Failed to connect MQTT client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Runs every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void simulateMachines() {
        try {
            if (mqttClient == null || !mqttClient.isConnected()) {
                System.out.println("⚠️ MQTT client not connected, attempting to reconnect...");
                initializeMqttClient();
                return;
            }

            System.out.println("🔄 Starting simulation cycle for 3 machines...");

            // Simulate 3 coffee machines
            for (int machineId = 1; machineId <= 3; machineId++) {
                // Generate random machine data with integer values
                Map<String, Object> messageMap = generateMachineData(machineId);

                try {
                    // Save analytics data to local database
                    MachineData machineData = new MachineData(
                            machineId,
                            (Integer) messageMap.get("facilityId"),
                            (String) messageMap.get("status"),
                            (Double) messageMap.get("temperature"),
                            (Double) messageMap.get("waterLevel"),
                            (Double) messageMap.get("milkLevel"),
                            (Double) messageMap.get("beansLevel"),
                            (Double) messageMap.get("sugarLevel"),
                            (String) messageMap.get("brewType"));

                    machineDataRepository.save(machineData);
                    System.out.println("💾 Saved analytics data for machine " + machineId);

                    // Convert to JSON for MQTT
                    String jsonMessage = objectMapper.writeValueAsString(messageMap);

                    // Publish to HiveMQ - let the backend handle real-time updates
                    String topic = "coffeemachine/" + machineId + "/data";
                    MqttMessage mqttMessage = new MqttMessage(jsonMessage.getBytes());
                    mqttMessage.setQos(1);
                    mqttClient.publish(topic, mqttMessage);

                    System.out.println("✅ Published to topic: " + topic + " → " + jsonMessage);

                } catch (Exception e) {
                    System.err.println("❌ Error processing machine " + machineId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error in simulation cycle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, Object> generateMachineData(int machineId) {
        Map<String, Object> messageMap = new LinkedHashMap<>();
        Random random = new Random();

        // Generate realistic coffee machine data with integer values
        messageMap.put("machineId", machineId);
        messageMap.put("facilityId", 1); // Default facility
        messageMap.put("status", random.nextBoolean() ? "ON" : "OFF");
        messageMap.put("temperature", 85 + random.nextInt(31)); // 85-115°C
        messageMap.put("waterLevel", 20 + random.nextInt(81)); // 20-100%
        messageMap.put("milkLevel", 20 + random.nextInt(81)); // 20-100%
        messageMap.put("beansLevel", 20 + random.nextInt(81)); // 20-100%
        messageMap.put("sugarLevel", 20 + random.nextInt(81)); // 20-100%

        // Generate brew type if machine is ON
        String status = (String) messageMap.get("status");
        if ("ON".equals(status)) {
            String[] brewTypes = { "AMERICANO", "LATTE", "BLACK_COFFEE", "CAPPUCCINO", "ESPRESSO" };
            String brewType = brewTypes[random.nextInt(brewTypes.length)];
            messageMap.put("brewType", brewType);
        } else {
            messageMap.put("brewType", "None");
        }

        messageMap.put("timestamp", LocalDateTime.now().toString());

        return messageMap;
    }
}
