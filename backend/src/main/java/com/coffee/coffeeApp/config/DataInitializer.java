package com.coffee.coffeeApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.coffee.coffeeApp.entity.User;
import com.coffee.coffeeApp.entity.CoffeeMachine;
import com.coffee.coffeeApp.entity.Facility;
import com.coffee.coffeeApp.repository.UserRepository;
import com.coffee.coffeeApp.repository.CoffeeMachineRepository;
import com.coffee.coffeeApp.repository.FacilityRepository;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeMachineRepository coffeeMachineRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if default admin user exists
        if (!userRepository.findByUsername("Ashutosh").isPresent()) {
            User adminUser = new User();
            adminUser.setUsername("Ashutosh");
            adminUser.setPassword(passwordEncoder.encode("p@ssword123"));
            adminUser.setRole("ROLE_ADMIN");
            adminUser.setIsActive(true);

            userRepository.save(adminUser);
            System.out.println("✅ Default admin user created: Ashutosh");
        }

        // Check if default facility exists
        if (facilityRepository.count() == 0) {
            Facility facility = new Facility();
            facility.setName("Main Coffee Shop");
            facility.setLocation("Ground Floor, Building A");
            facility.setIsActive(true);
            facility.setCreationDate(LocalDateTime.now());
            facility.setLastUpdate(LocalDateTime.now());

            Facility savedFacility = facilityRepository.save(facility);
            System.out.println("✅ Default facility created: " + savedFacility.getName());
        }

        // Check if coffee machines exist
        if (coffeeMachineRepository.count() == 0) {
            Facility facility = facilityRepository.findAll().get(0);

            for (int i = 1; i <= 3; i++) {
                CoffeeMachine machine = new CoffeeMachine();
                machine.setFacilityId(facility.getId());
                machine.setName("Coffee Machine " + i);
                machine.setStatus("ON");
                machine.setTemperature(95.0f);
                machine.setWaterLevel(100.0f);
                machine.setMilkLevel(100.0f);
                machine.setBeansLevel(100.0f);
                machine.setSugarLevel(100.0f);
                machine.setIsActive(true);
                machine.setCreationDate(LocalDateTime.now());
                machine.setLastUpdate(LocalDateTime.now());

                CoffeeMachine savedMachine = coffeeMachineRepository.save(machine);
                System.out.println("✅ Coffee machine created: " + savedMachine.getName());
            }
        }
    }
}
