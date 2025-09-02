package com.coffee.coffeeApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.coffee.coffeeApp.service.FacilityService;
import com.coffee.coffeeApp.dto.DashboardSummaryDto;
import com.coffee.coffeeApp.dto.FacilityDto;
import com.coffee.coffeeApp.service.UsageHistoryService;
import com.coffee.coffeeApp.service.AlertLogService;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import java.util.HashMap;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FacilityService facilityService;
    private final UsageHistoryService usageHistoryService;
    private final AlertLogService alertLogService;

    public AdminController(FacilityService facilityService, UsageHistoryService usageHistoryService,
            AlertLogService alertLogService) {
        this.facilityService = facilityService;
        this.usageHistoryService = usageHistoryService;
        this.alertLogService = alertLogService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDto> getAdminDashboard() {
        // Get aggregated data across all facilities
        DashboardSummaryDto summary = new DashboardSummaryDto();
        // Implementation would aggregate data from all facilities
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        // Return all facilities with detailed information
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Object> getAnalytics() {
        return ResponseEntity.ok(usageHistoryService.getUsageByBrewTypeStats());
    }

    @GetMapping("/machine-performance")
    public ResponseEntity<Object> getMachinePerformance() {
        return ResponseEntity.ok(usageHistoryService.getUsageByMachineStats());
    }

    @GetMapping("/usage-history")
    public ResponseEntity<Object> getAggregatedUsage() {
        return ResponseEntity.ok(usageHistoryService.getDailyUsageStatistics());
    }

    @GetMapping("/alerts")
    public ResponseEntity<Object> getAllAlertsSummary() {
        return ResponseEntity.ok(alertLogService.getAlertStatistics());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getAdminSummary() {
        Map<String, Object> res = new HashMap<>();
        var facilities = facilityService.getAllFacilities();
        long totalFacilities = facilities.size();
        long totalMachines = facilities.stream().mapToLong(FacilityDto::getTotalMachines).sum();
        long activeMachines = facilities.stream().mapToLong(FacilityDto::getActiveMachines).sum();
        long totalAlerts = alertLogService.getAlertStatistics().getTotalAlerts();

        res.put("totalFacilities", totalFacilities);
        res.put("totalMachines", totalMachines);
        res.put("activeMachines", activeMachines);
        res.put("totalAlerts", totalAlerts);
        // Per-facility (Pune, Mumbai) cards
        res.put("facilities", facilities);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/usage-today")
    public ResponseEntity<Map<String, Object>> getUsageToday() {
        long todayTotal = usageHistoryService.getTodayUsage().size();
        Map<String, Object> res = new HashMap<>();
        res.put("todayTotal", todayTotal);
        return ResponseEntity.ok(res);
    }
}
