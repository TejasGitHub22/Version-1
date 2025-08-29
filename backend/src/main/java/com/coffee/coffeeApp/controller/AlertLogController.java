package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.AlertLogDto;
import com.coffee.coffeeApp.service.AlertLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertLogController {

    @Autowired
    private AlertLogService alertLogService;

    @PostMapping
    public ResponseEntity<AlertLogDto> createAlert(@Valid @RequestBody AlertLogDto alertDto) {
        AlertLogDto createdAlert = alertLogService.createAlert(alertDto);
        return ResponseEntity.ok(createdAlert);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertLogDto> getAlertById(@PathVariable String id) {
        return alertLogService.getAlertById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AlertLogDto>> getAllActiveAlerts() {
        List<AlertLogDto> alerts = alertLogService.getAllActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<AlertLogDto>> getAlertsByMachine(@PathVariable String machineId) {
        List<AlertLogDto> alerts = alertLogService.getAlertsByMachine(machineId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/type/{alertType}")
    public ResponseEntity<List<AlertLogDto>> getAlertsByType(@PathVariable String alertType) {
        List<AlertLogDto> alerts = alertLogService.getAlertsByType(alertType);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/critical")
    public ResponseEntity<List<AlertLogDto>> getCriticalAlerts() {
        List<AlertLogDto> alerts = alertLogService.getCriticalAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/supply")
    public ResponseEntity<List<AlertLogDto>> getSupplyAlerts() {
        List<AlertLogDto> alerts = alertLogService.getSupplyAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AlertLogDto>> getRecentAlerts(@RequestParam(defaultValue = "24") int hours) {
        List<AlertLogDto> alerts = alertLogService.getRecentAlerts(hours);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/today")
    public ResponseEntity<List<AlertLogDto>> getTodayAlerts() {
        List<AlertLogDto> alerts = alertLogService.getTodayAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PreAuthorize("hasRole('Technician')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable String id) {
        alertLogService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}