package com.coffee.coffeeApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.coffee.coffeeApp.entity.User;
import com.coffee.coffeeApp.service.CoffeeMachineService;
import com.coffee.coffeeApp.dto.DashboardSummaryDto;
import com.coffee.coffeeApp.dto.CoffeeMachineDto;

import java.util.List;

@RestController
@RequestMapping("/api/facility")
@PreAuthorize("hasRole('FACILITY')")
public class FacilityUserController {

    private final CoffeeMachineService coffeeMachineService;

    public FacilityUserController(CoffeeMachineService coffeeMachineService) {
        this.coffeeMachineService = coffeeMachineService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDto> getFacilityDashboard(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Get dashboard data for the user's assigned facility only
        DashboardSummaryDto summary = new DashboardSummaryDto();
        // Implementation would get data only for the specific facility
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/machines")
    public ResponseEntity<List<CoffeeMachineDto>> getFacilityMachines(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Integer facilityId = user.getFacility().getId();

        // Return machines only for the user's assigned facility
        List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesByFacilityId(String.valueOf(facilityId));
        return ResponseEntity.ok(machines);
    }

    @GetMapping("/info")
    public ResponseEntity<Object> getFacilityInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Return information about the user's assigned facility
        return ResponseEntity.ok(user.getFacility());
    }
}
