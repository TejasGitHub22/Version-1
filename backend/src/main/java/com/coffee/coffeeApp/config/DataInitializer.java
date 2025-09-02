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
            adminUser.setEmail("admin@coffeeapp.com");
            adminUser.setPassword(passwordEncoder.encode("p@ssword123"));
            adminUser.setRole("ADMIN");
            adminUser.setIsActive(true);

            userRepository.save(adminUser);
            System.out.println("✅ Default admin user created: Ashutosh");
        }

        // Seed facilities and machines if empty: Pune and Mumbai with 2 offices each, 3
        // machines per office
        if (facilityRepository.count() == 0 && coffeeMachineRepository.count() == 0) {
            String[][] facilitySeeds = new String[][] {
                    { "Pune - Office 1", "Pune" },
                    { "Pune - Office 2", "Pune" },
                    { "Mumbai - Office 1", "Mumbai" },
                    { "Mumbai - Office 2", "Mumbai" }
            };

            for (String[] seed : facilitySeeds) {
                Facility facility = new Facility();
                facility.setName(seed[0]);
                facility.setLocation(seed[1]);
                facility.setIsActive(true);
                facility.setCreationDate(LocalDateTime.now());
                facility.setLastUpdate(LocalDateTime.now());
                Facility savedFacility = facilityRepository.save(facility);
                System.out.println("✅ Facility created: " + savedFacility.getName());

                // Three machines per office
                for (int i = 1; i <= 3; i++) {
                    CoffeeMachine machine = new CoffeeMachine();
                    machine.setFacilityId(savedFacility.getId());
                    machine.setName(savedFacility.getName() + " - Machine " + i);
                    machine.setStatus("ON");
                    machine.setTemperature(92.0f);
                    machine.setWaterLevel(100.0f);
                    machine.setMilkLevel(100.0f);
                    machine.setBeansLevel(100.0f);
                    machine.setSugarLevel(100.0f);
                    machine.setIsActive(true);
                    machine.setCreationDate(LocalDateTime.now());
                    machine.setLastUpdate(LocalDateTime.now());
                    CoffeeMachine savedMachine = coffeeMachineRepository.save(machine);
                    System.out.println("✅ Machine created: " + savedMachine.getName());
                }
            }
        }

        // Seed two technician users mapped to facilities if none exist
        try {
            if (facilityRepository.count() > 0) {
                Facility puneFacility = facilityRepository.findByLocationAndIsActiveTrue("Pune").stream().findFirst()
                        .orElse(null);
                Facility mumbaiFacility = facilityRepository.findByLocationAndIsActiveTrue("Mumbai").stream()
                        .findFirst().orElse(null);

                userRepository.findByUsername("pune.tech").or(() -> {
                    if (puneFacility != null) {
                        User u = new User();
                        u.setUsername("pune.tech");
                        u.setEmail("pune.tech@coffeeapp.com");
                        u.setPassword(passwordEncoder.encode("tech@123"));
                        u.setRole("FACILITY");
                        u.setFacility(puneFacility);
                        u.setIsActive(true);
                        userRepository.save(u);
                        System.out.println("✅ Technician seeded: pune.tech (Pune)");
                    }
                    return java.util.Optional.empty();
                });

                userRepository.findByUsername("mumbai.tech").or(() -> {
                    if (mumbaiFacility != null) {
                        User u = new User();
                        u.setUsername("mumbai.tech");
                        u.setEmail("mumbai.tech@coffeeapp.com");
                        u.setPassword(passwordEncoder.encode("tech@123"));
                        u.setRole("FACILITY");
                        u.setFacility(mumbaiFacility);
                        u.setIsActive(true);
                        userRepository.save(u);
                        System.out.println("✅ Technician seeded: mumbai.tech (Mumbai)");
                    }
                    return java.util.Optional.empty();
                });
            }
        } catch (Exception ignored) {
        }
    }
}
