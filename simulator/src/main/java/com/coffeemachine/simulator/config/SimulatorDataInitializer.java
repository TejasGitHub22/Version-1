package com.coffeemachine.simulator.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SimulatorDataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎯 MQTT Coffee Machine Simulator starting up...");
        System.out.println("📡 Will simulate 3 coffee machines and publish data every 5 seconds");
        System.out.println("🌐 Data will be sent to backend via MQTT for analytics and storage");
    }
}
