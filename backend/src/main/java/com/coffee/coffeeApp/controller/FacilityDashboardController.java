package com.coffee.coffeeApp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffee.coffeeApp.dto.AlertLogDto;
import com.coffee.coffeeApp.dto.CoffeeMachineDto;
import com.coffee.coffeeApp.dto.FacilityMachinesResponseDto;
import com.coffee.coffeeApp.dto.FacilityUsageSummaryDto;
import com.coffee.coffeeApp.entity.UsageHistory;
import com.coffee.coffeeApp.entity.User;
import com.coffee.coffeeApp.repository.AlertLogRepository;
import com.coffee.coffeeApp.repository.CoffeeMachineRepository;
import com.coffee.coffeeApp.repository.UsageHistoryRepository;
import com.coffee.coffeeApp.service.CoffeeMachineService;

@RestController
@RequestMapping("/api/facility")
@PreAuthorize("hasRole('FACILITY')")
public class FacilityDashboardController {

    private final CoffeeMachineService coffeeMachineService;
    private final CoffeeMachineRepository coffeeMachineRepository;
    private final AlertLogRepository alertLogRepository;
    private final UsageHistoryRepository usageHistoryRepository;

    public FacilityDashboardController(CoffeeMachineService coffeeMachineService,
            CoffeeMachineRepository coffeeMachineRepository,
            AlertLogRepository alertLogRepository,
            UsageHistoryRepository usageHistoryRepository) {
        this.coffeeMachineService = coffeeMachineService;
        this.coffeeMachineRepository = coffeeMachineRepository;
        this.alertLogRepository = alertLogRepository;
        this.usageHistoryRepository = usageHistoryRepository;
    }

    @GetMapping("/{facilityId}/machines")
    public ResponseEntity<FacilityMachinesResponseDto> getFacilityMachines(@PathVariable String facilityId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getFacility() == null || !String.valueOf(user.getFacility().getId()).equals(facilityId)) {
            return ResponseEntity.status(403).build();
        }

        List<CoffeeMachineDto> machines = coffeeMachineService.getMachinesByFacilityId(facilityId);
        List<AlertLogDto> alerts = alertLogRepository.findSupplyAlerts().stream()
                .filter(a -> a.getFacilityId() != null && a.getFacilityId().equals(Integer.parseInt(facilityId)))
                .map(a -> new AlertLogDto(String.valueOf(a.getId()), String.valueOf(a.getMachineId()), a.getAlertType(),
                        a.getMessage()))
                .collect(Collectors.toList());

        FacilityMachinesResponseDto response = new FacilityMachinesResponseDto(machines, alerts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{facilityId}/usage-history")
    public ResponseEntity<FacilityUsageSummaryDto> getFacilityUsage(@PathVariable String facilityId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getFacility() == null || !String.valueOf(user.getFacility().getId()).equals(facilityId)) {
            return ResponseEntity.status(403).build();
        }

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<UsageHistory> recent = usageHistoryRepository.findRecentUsage(since);

        List<FacilityUsageSummaryDto.UsageRow> rows = recent.stream()
                .filter(uh -> uh.getMachineId() != null &&
                        coffeeMachineRepository.findById(uh.getMachineId())
                                .map(m -> m.getFacilityId().equals(Integer.parseInt(facilityId))).orElse(false))
                .map(u -> new FacilityUsageSummaryDto.UsageRow(u.getBrewType(), 1, u.getTimestamp()))
                .collect(Collectors.toList());

        FacilityUsageSummaryDto summary = new FacilityUsageSummaryDto(rows, since);
        return ResponseEntity.ok(summary);
    }
}


