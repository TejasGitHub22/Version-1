package com.coffeemachine.simulator.service;

import com.coffeemachine.simulator.model.CoffeeMachine;
import com.coffeemachine.simulator.model.MachineData;
import com.coffeemachine.simulator.repository.CoffeeMachineRepository;
import com.coffeemachine.simulator.repository.MachineDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MachineSimulatorService {

    private final CoffeeMachineRepository coffeeMachineRepository;
    private final MachineDataRepository machineDataRepository;
    private final ObjectMapper objectMapper;
    
    MqttClient mqttClient;

    // MQTT Config from application.properties
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    
    private String clientId = "mqtt-simulator-client" + UUID.randomUUID();
    
    @Value("${mqtt.username}")
    private String username = "your-hivemq-username";
    
    @Value("${mqtt.password}")
    private String password = "your-hivemq-password";

    public MachineSimulatorService(CoffeeMachineRepository coffeeMachineRepository,
                                   MachineDataRepository machineDataRepository,
                                   ObjectMapper objectMapper) {
        this.coffeeMachineRepository = coffeeMachineRepository;
        this.machineDataRepository = machineDataRepository;
        this.objectMapper = objectMapper;
    }

    // Runs every 5 seconds
    public void simulateMachines() {
        Integer maxId = coffeeMachineRepository.findMaxId();
        if (maxId == null) return;

        for (int id = 1; id <= maxId; id++) {
            Optional<CoffeeMachine> optionalMachine = coffeeMachineRepository.findById(id);

            if (optionalMachine.isPresent()) continue;

            CoffeeMachine machine = optionalMachine.get();

            // Check if machine is active
            if (!machine.getIsActive()) continue;

            // Try to brew
            String brewType = tryBrew(machine);

            // Update lastUpdate timestamp
            machine.setLastUpdate(LocalDateTime.now());
            coffeeMachineRepository.save(machine);

            // Prepare message
            Map<String, Object> messageMap = new LinkedHashMap<>();
            messageMap.put("machineId", machine.getId());
            messageMap.put("facilityId", machine.getFacilityId());
            messageMap.put("status", machine.getStatus());
            messageMap.put("temperature", machine.getTemperature());
            messageMap.put("waterLevel", machine.getWaterLevel());
            messageMap.put("milkLevel", machine.getMilkLevel());
            messageMap.put("beansLevel", machine.getBeansLevel());
            messageMap.put("brewType", brewType);
            messageMap.put("timestamp", LocalDateTime.now().toString());

            try {
                // Convert to JSON
                String jsonMessage = objectMapper.writeValueAsString(messageMap);

                // Save message to DB as string
                MachineData machineData = objectMapper.readValue(jsonMessage, MachineData.class);
                machineDataRepository.save(machineData);

                // Publish to HiveMQ
                String topic = "coffeemachine/" + machine.getId() + "/data";
                MqttMessage mqttMessage = new MqttMessage(jsonMessage.getBytes());
                mqttMessage.setQos(1);
                mqttClient.publish(topic, mqttMessage);

                System.out.println("✅ Published to topic: " + topic + " → " + jsonMessage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String tryBrew(CoffeeMachine machine) {
        // Brew recipes
        Map<String, double[]> recipes = new HashMap<>();
        recipes.put("AMERICANO", new double[]{6.0, 0.0, 4.0, 0.5});
        recipes.put("LATTE", new double[]{2.0, 6.0, 4.0, 0.5});
        recipes.put("BLACK_COFFEE", new double[]{8.0, 0.0, 4.0, 0.0});
        recipes.put("CAPPUCCINO", new double[]{3.0, 3.0, 4.0, 0.5});

        List<String> brewTypes = new ArrayList<>(recipes.keySet());
        String selected = brewTypes.get(new Random().nextInt(brewTypes.size()));

        double[] recipe = recipes.get(selected);

        // Check resources
        if (machine.getWaterLevel() >= recipe[0] &&
            machine.getMilkLevel() >= recipe[1] &&
            machine.getBeansLevel() >= recipe[2]) {

            // Deduct resources
            machine.setWaterLevel(machine.getWaterLevel() - recipe[0]);
            machine.setMilkLevel(machine.getMilkLevel() - recipe[1]);
            machine.setBeansLevel(machine.getBeansLevel() - recipe[2]);

            return selected;
        }

        return null; // Not enough resources
    }
}
