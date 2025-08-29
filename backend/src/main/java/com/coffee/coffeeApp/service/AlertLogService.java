package com.coffee.coffeeApp.service;

import com.coffee.coffeeApp.dto.AlertLogDto;
import com.coffee.coffeeApp.entity.AlertLog;
import com.coffee.coffeeApp.repository.AlertLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlertLogService {
    
    @Autowired
    private AlertLogRepository alertLogRepository;
    
    // Create generic alert
    public AlertLogDto createAlert(String machineId, String alertType, String message) {
        AlertLog alert = new AlertLog(Integer.parseInt(machineId), alertType, message);
        AlertLog savedAlert = alertLogRepository.save(alert);
        return convertToDto(savedAlert);
    }
    
    // Create low water alert
    public AlertLogDto createLowWaterAlert(String machineId, float currentLevel) {
        AlertLog alert = AlertLog.createLowWaterAlert(Integer.parseInt(machineId), currentLevel);
        AlertLog savedAlert = alertLogRepository.save(alert);
        return convertToDto(savedAlert);
    }
    
    // Create low milk alert
    public AlertLogDto createLowMilkAlert(String machineId, float currentLevel) {
        AlertLog alert = AlertLog.createLowMilkAlert(Integer.parseInt(machineId), currentLevel);
        AlertLog savedAlert = alertLogRepository.save(alert);
        return convertToDto(savedAlert);
    }
    
    // Create low beans alert
    public AlertLogDto createLowBeansAlert(String machineId, float currentLevel) {
        AlertLog alert = AlertLog.createLowBeansAlert(Integer.parseInt(machineId), currentLevel);
        AlertLog savedAlert = alertLogRepository.save(alert);
        return convertToDto(savedAlert);
    }
    
    // Create malfunction alert
    public AlertLogDto createMalfunctionAlert(String machineId, String issue) {
        AlertLog alert = AlertLog.createMalfunctionAlert(Integer.parseInt(machineId), issue);
        AlertLog savedAlert = alertLogRepository.save(alert);
        return convertToDto(savedAlert);
    }
    
    // Create offline alert
    public AlertLogDto createOfflineAlert(String machineId, String reason) {
        String message = String.format("Machine went offline: %s", reason);
        AlertLog alert = new AlertLog(Integer.parseInt(machineId), "OFFLINE", message);
        AlertLog savedAlert = alertLogRepository.save(alert);
        return convertToDto(savedAlert);
    }
    
    // Get alert by ID
    @Transactional(readOnly = true)
    public Optional<AlertLogDto> getAlertById(String id) {
        return alertLogRepository.findById(Integer.parseInt(id))
                .filter(AlertLog::getIsActive)
                .map(this::convertToDto);
    }
    
    // Get all active alerts
    @Transactional(readOnly = true)
    public List<AlertLogDto> getAllActiveAlerts() {
        return alertLogRepository.findByIsActiveTrueOrderByTimestampDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get alerts by machine
    @Transactional(readOnly = true)
    public List<AlertLogDto> getAlertsByMachine(String machineId) {
        return alertLogRepository.findByMachineIdAndIsActiveTrueOrderByTimestampDesc(Integer.parseInt(machineId))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Create alert from DTO
    public AlertLogDto createAlert(AlertLogDto alertDto) {
        if (alertDto.getId() == null || alertDto.getId().isEmpty()) {
            alertDto.setId(UUID.randomUUID().toString());
        }
        AlertLog alert = convertToEntity(alertDto);
        AlertLog savedAlert = alertLogRepository.save(alert);
        return convertToDto(savedAlert);
    }

    // Get alerts by type
    @Transactional(readOnly = true)
    public List<AlertLogDto> getAlertsByType(String alertType) {
        return alertLogRepository.findByAlertTypeAndIsActiveTrueOrderByTimestampDesc(alertType)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get recent alerts
    @Transactional(readOnly = true)
    public List<AlertLogDto> getRecentAlerts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return alertLogRepository.findRecentAlerts(since)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get critical alerts
    @Transactional(readOnly = true)
    public List<AlertLogDto> getCriticalAlerts() {
        return alertLogRepository.findCriticalAlerts()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get supply alerts
    @Transactional(readOnly = true)
    public List<AlertLogDto> getSupplyAlerts() {
        return alertLogRepository.findSupplyAlerts()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get today's alerts
    @Transactional(readOnly = true)
    public List<AlertLogDto> getTodayAlerts() {
        return alertLogRepository.findTodayAlerts()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get unresolved alerts
    @Transactional(readOnly = true)
    public List<AlertLogDto> getUnresolvedAlerts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return alertLogRepository.findUnresolvedAlerts(since)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get alerts requiring immediate attention
    @Transactional(readOnly = true)
    public List<AlertLogDto> getAlertsRequiringAttention() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return alertLogRepository.findAlertsRequiringAttention(since)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Resolve alert (soft delete)
    public void resolveAlert(String alertId) {
        AlertLog alert = alertLogRepository.findById(Integer.parseInt(alertId))
                .filter(AlertLog::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        
        alert.setIsActive(false);
        alertLogRepository.save(alert);
    }
    
    // Resolve all alerts of specific type for a machine
    public void resolveAlertsByMachineAndType(String machineId, String alertType) {
        alertLogRepository.resolveAlertsByMachineAndType(Integer.parseInt(machineId), alertType);
    }
    
    // Get alert statistics
    @Transactional(readOnly = true)
    public AlertStatistics getAlertStatistics() {
        long totalAlerts = alertLogRepository.countByIsActiveTrue();
        long criticalAlerts = alertLogRepository.countCriticalAlerts();
        long supplyAlerts = alertLogRepository.countSupplyAlerts();
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        Object[] summary = alertLogRepository.getAlertSummary(today);
        long todayAlerts = summary != null ? ((Number) summary[2]).longValue() : 0;
        
        return new AlertStatistics(totalAlerts, criticalAlerts, supplyAlerts, todayAlerts);
    }
    
    // Get alert frequency by facility
    @Transactional(readOnly = true)
    public List<FacilityAlertStats> getAlertFrequencyByFacility() {
        List<Object[]> stats = alertLogRepository.getAlertFrequencyByFacility();
        return stats.stream()
                .map(row -> new FacilityAlertStats(
                    (String) row[0], // facilityId
                    ((Number) row[1]).longValue() // alertCount
                ))
                .collect(Collectors.toList());
    }
    
    // Get machines with frequent alerts
    @Transactional(readOnly = true)
    public List<MachineAlertStats> getMachinesWithFrequentAlerts(int hours, long threshold) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<Object[]> stats = alertLogRepository.findMachinesWithFrequentAlerts(since, threshold);
        return stats.stream()
                .map(row -> new MachineAlertStats(
                    (String) row[0], // machineId
                    ((Number) row[1]).longValue() // alertCount
                ))
                .collect(Collectors.toList());
    }
    
    // Get alert trends
    @Transactional(readOnly = true)
    public List<AlertTrend> getAlertTrends(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> trends = alertLogRepository.getAlertTrends(since);
        return trends.stream()
                .map(row -> new AlertTrend(
                    (java.sql.Date) row[0], // date
                    ((Number) row[1]).longValue() // alertCount
                ))
                .collect(Collectors.toList());
    }
    
    // Check for duplicate alerts
    @Transactional(readOnly = true)
    public boolean hasDuplicateAlert(String machineId, String alertType, int withinMinutes) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(withinMinutes);
        
        List<AlertLog> duplicates = alertLogRepository.findDuplicateAlerts(
        		Integer.parseInt(machineId), alertType, startTime, endTime);
        
        return !duplicates.isEmpty();
    }
    
    // Bulk create alerts (for testing or batch operations)
    public List<AlertLogDto> createBulkAlerts(List<AlertLogDto> alertDtos) {
        List<AlertLog> alerts = alertDtos.stream()
                .map(dto -> {
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        dto.setId(UUID.randomUUID().toString());
                    }
                    return convertToEntity(dto);
                })
                .collect(Collectors.toList());
        
        List<AlertLog> savedAlerts = alertLogRepository.saveAll(alerts);
        return savedAlerts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Delete alert (soft delete)
    public void deleteAlert(String alertId) {
        AlertLog alert = alertLogRepository.findById(Integer.parseInt(alertId))
                .filter(AlertLog::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        
        alert.setIsActive(false);
        alertLogRepository.save(alert);
    }
    
    // Conversion methods
    private AlertLogDto convertToDto(AlertLog alert) {
        AlertLogDto dto = new AlertLogDto();
        dto.setId(String.valueOf(alert.getId()));
        dto.setMachineId(String.valueOf(alert.getMachineId()));
        dto.setAlertType(alert.getAlertType());
        dto.setMessage(alert.getMessage());
        dto.setTimestamp(alert.getTimestamp());
        dto.setIsActive(alert.getIsActive());
        dto.setCreationDate(alert.getCreationDate());
        dto.setLastUpdate(alert.getLastUpdate());
        
        // Set computed fields
        dto.setIsResolved(!alert.getIsActive());
        
        return dto;
    }
    
    private AlertLog convertToEntity(AlertLogDto dto) {
        AlertLog alert = new AlertLog();
        alert.setId(Integer.parseInt(dto.getId()));
        alert.setMachineId(Integer.parseInt(dto.getMachineId()));
        alert.setAlertType(dto.getAlertType());
        alert.setMessage(dto.getMessage());
        alert.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        alert.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return alert;
    }
    
    // Inner classes for statistics
    public static class AlertStatistics {
        private final long totalAlerts;
        private final long criticalAlerts;
        private final long supplyAlerts;
        private final long todayAlerts;
        
        public AlertStatistics(long totalAlerts, long criticalAlerts, long supplyAlerts, long todayAlerts) {
            this.totalAlerts = totalAlerts;
            this.criticalAlerts = criticalAlerts;
            this.supplyAlerts = supplyAlerts;
            this.todayAlerts = todayAlerts;
        }
        
        // Getters
        public long getTotalAlerts() { return totalAlerts; }
        public long getCriticalAlerts() { return criticalAlerts; }
        public long getSupplyAlerts() { return supplyAlerts; }
        public long getTodayAlerts() { return todayAlerts; }
    }
    
    public static class FacilityAlertStats {
        private final String facilityId;
        private final long alertCount;
        
        public FacilityAlertStats(String facilityId, long alertCount) {
            this.facilityId = facilityId;
            this.alertCount = alertCount;
        }
        
        public String getFacilityId() { return facilityId; }
        public long getAlertCount() { return alertCount; }
    }
    
    public static class MachineAlertStats {
        private final String machineId;
        private final long alertCount;
        
        public MachineAlertStats(String machineId, long alertCount) {
            this.machineId = machineId;
            this.alertCount = alertCount;
        }
        
        public String getMachineId() { return machineId; }
        public long getAlertCount() { return alertCount; }
    }
    
    public static class AlertTrend {
        private final java.sql.Date date;
        private final long alertCount;
        
        public AlertTrend(java.sql.Date date, long alertCount) {
            this.date = date;
            this.alertCount = alertCount;
        }
        
        public java.sql.Date getDate() { return date; }
        public long getAlertCount() { return alertCount; }
    }
}