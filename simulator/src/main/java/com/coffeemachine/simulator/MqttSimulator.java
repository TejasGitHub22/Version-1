package com.coffeemachine.simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.coffeemachine.simulator.service.MachineSimulatorService;

@SpringBootApplication
@EnableScheduling
public class MqttSimulator implements CommandLineRunner {

	@Autowired
    private MachineSimulatorService simulatorService;


    public static void main(String[] args) {
        SpringApplication.run(MqttSimulator.class, args);
    }

    @Override
    public void run(String... args) {
        simulatorService.simulateMachines();
    }
}
