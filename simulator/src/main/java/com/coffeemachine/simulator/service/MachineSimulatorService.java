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

    // In-memory machine state for deterministic, gradual changes
    private static class MachineState {
        int facilityId;
        String status;
        int temperature;
        int water;
        int milk;
        int beans;
        int sugar;
    }

    private final Map<Integer, MachineState> idToState = new HashMap<>();

    private void ensureStatesInitialized() {
        if (!idToState.isEmpty()) return;
        // Machines 1-3: Pune (facility 1), 4-6: Mumbai (facility 2)
        for (int id = 1; id <= 6; id++) {
            MachineState s = new MachineState();
            s.facilityId = (id <= 3) ? 1 : 2;
            s.status = "ON";
            s.temperature = 92; // starting temp
            s.water = 100;
            s.milk = 100;
            s.beans = 100;
            s.sugar = 100;
            idToState.put(id, s);
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

            ensureStatesInitialized();
            System.out.println("🔄 Starting simulation cycle for 6 machines...");

            for (int machineId = 1; machineId <= 6; machineId++) {
                Map<String, Object> messageMap = generateMachineData(machineId);

                try {
                    // Save analytics data to local database
                    MachineData machineData = new MachineData(
                            machineId,
                            (Integer) messageMap.get("facilityId"),
                            (String) messageMap.get("status"),
                            ((Number) messageMap.get("temperature")).doubleValue(),
                            ((Number) messageMap.get("waterLevel")).doubleValue(),
                            ((Number) messageMap.get("milkLevel")).doubleValue(),
                            ((Number) messageMap.get("beansLevel")).doubleValue(),
                            ((Number) messageMap.get("sugarLevel")).doubleValue(),
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
        ensureStatesInitialized();
        MachineState s = idToState.get(machineId);
        if (s == null) {
            s = new MachineState();
            s.facilityId = (machineId <= 3) ? 1 : 2;
            s.status = "ON";
            s.temperature = 92;
            s.water = s.milk = s.beans = s.sugar = 100;
            idToState.put(machineId, s);
        }

        // Simple logic: if ON, gradually decrease supplies; temp fluctuates a bit
        if ("ON".equals(s.status)) {
            s.temperature = Math.max(85, Math.min(110, s.temperature + (new Random().nextBoolean() ? 1 : -1)));
            s.water = Math.max(0, s.water - 3);
            s.milk = Math.max(0, s.milk - 2);
            s.beans = Math.max(0, s.beans - 1);
            s.sugar = Math.max(0, s.sugar - 1);
        }

        messageMap.put("machineId", machineId);
        messageMap.put("facilityId", s.facilityId);
        messageMap.put("status", s.status);
        messageMap.put("temperature", s.temperature);
        messageMap.put("waterLevel", s.water);
        messageMap.put("milkLevel", s.milk);
        messageMap.put("beansLevel", s.beans);
        messageMap.put("sugarLevel", s.sugar);

        if ("ON".equals(s.status)) {
            String[] brewTypes = { "AMERICANO", "LATTE", "BLACK_COFFEE", "CAPPUCCINO", "ESPRESSO" };
            String brewType = brewTypes[Math.abs(Objects.hash(machineId, System.currentTimeMillis())) % brewTypes.length];
            messageMap.put("brewType", brewType);
        } else {
            messageMap.put("brewType", "None");
        }

        messageMap.put("timestamp", LocalDateTime.now().toString());
        return messageMap;
    }
}
