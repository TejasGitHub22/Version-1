package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class AlertLogDto {
    
    private String id;
    
    @NotBlank(message = "Machine ID is required")
    private String machineId;
    
    @NotBlank(message = "Alert type is required")
    @Pattern(regexp = "LOW_WATER|LOW_MILK|LOW_BEANS|MALFUNCTION|OFFLINE|EMERGENCY|MAINTENANCE|TEMPERATURE", 
             message = "Invalid alert type")
    private String alertType;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    private Boolean isActive;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Additional fields for API responses
    private String machineFacilityId;
    private String machineFacilityName;
    private String machineFacilityLocation;
    private String machineStatus;
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private String category; // SUPPLY, TECHNICAL, MAINTENANCE
    private Boolean isResolved;
    private Boolean requiresImmediateAttention;
    
    // Constructors
    public AlertLogDto() {}
    
    public AlertLogDto(String id, String machineId, String alertType, String message) {
        this.id = id;
        this.machineId = machineId;
        this.alertType = alertType;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
        this.isResolved = false;
        this.setSeverityAndCategory();
    }
    
    // Business logic methods
    public Boolean getIsCriticalAlert() {
        return "MALFUNCTION".equalsIgnoreCase(alertType) || 
               "OFFLINE".equalsIgnoreCase(alertType) ||
               "EMERGENCY".equalsIgnoreCase(alertType);
    }
    
    public Boolean getIsSupplyAlert() {
        return "LOW_WATER".equalsIgnoreCase(alertType) || 
               "LOW_MILK".equalsIgnoreCase(alertType) ||
               "LOW_BEANS".equalsIgnoreCase(alertType);
    }
    
    public Boolean getIsRecentAlert() {
        return timestamp != null && timestamp.isAfter(LocalDateTime.now().minusHours(24));
    }
    
    private void setSeverityAndCategory() {
        if (alertType != null) {
            switch (alertType.toUpperCase()) {
                case "MALFUNCTION":
                case "EMERGENCY":
                case "OFFLINE":
                    this.severity = "CRITICAL";
                    this.category = "TECHNICAL";
                    this.requiresImmediateAttention = true;
                    break;
                case "LOW_WATER":
                case "LOW_MILK":
                case "LOW_BEANS":
                    this.severity = "HIGH";
                    this.category = "SUPPLY";
                    this.requiresImmediateAttention = true;
                    break;
                case "MAINTENANCE":
                    this.severity = "MEDIUM";
                    this.category = "MAINTENANCE";
                    this.requiresImmediateAttention = false;
                    break;
                case "TEMPERATURE":
                    this.severity = "MEDIUM";
                    this.category = "TECHNICAL";
                    this.requiresImmediateAttention = false;
                    break;
                default:
                    this.severity = "LOW";
                    this.category = "GENERAL";
                    this.requiresImmediateAttention = false;
            }
        }
    }
    
    // Static factory methods for common alerts
    public static AlertLogDto createLowWaterAlert(String id, String machineId, float currentLevel) {
        String message = String.format("Water level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLogDto(id, machineId, "LOW_WATER", message);
    }
    
    public static AlertLogDto createLowMilkAlert(String id, String machineId, float currentLevel) {
        String message = String.format("Milk level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLogDto(id, machineId, "LOW_MILK", message);
    }
    
    public static AlertLogDto createLowBeansAlert(String id, String machineId, float currentLevel) {
        String message = String.format("Coffee beans level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLogDto(id, machineId, "LOW_BEANS", message);
    }
    
    public static AlertLogDto createMalfunctionAlert(String id, String machineId, String issue) {
        String message = String.format("Machine malfunction detected: %s. Maintenance required.", issue);
        return new AlertLogDto(id, machineId, "MALFUNCTION", message);
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getMachineId() {
        return machineId;
    }
    
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
        this.setSeverityAndCategory();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public String getMachineFacilityId() {
        return machineFacilityId;
    }
    
    public void setMachineFacilityId(String machineFacilityId) {
        this.machineFacilityId = machineFacilityId;
    }
    
    public String getMachineFacilityName() {
        return machineFacilityName;
    }
    
    public void setMachineFacilityName(String machineFacilityName) {
        this.machineFacilityName = machineFacilityName;
    }
    
    public String getMachineFacilityLocation() {
        return machineFacilityLocation;
    }
    
    public void setMachineFacilityLocation(String machineFacilityLocation) {
        this.machineFacilityLocation = machineFacilityLocation;
    }
    
    public String getMachineStatus() {
        return machineStatus;
    }
    
    public void setMachineStatus(String machineStatus) {
        this.machineStatus = machineStatus;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Boolean getIsResolved() {
        return isResolved;
    }
    
    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }
    
    public Boolean getRequiresImmediateAttention() {
        return requiresImmediateAttention;
    }
    
    public void setRequiresImmediateAttention(Boolean requiresImmediateAttention) {
        this.requiresImmediateAttention = requiresImmediateAttention;
    }
    
    @Override
    public String toString() {
        return "AlertLogDto{" +
                "id='" + id + '\'' +
                ", machineId='" + machineId + '\'' +
                ", alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                ", category='" + category + '\'' +
                ", isResolved=" + isResolved +
                ", requiresImmediateAttention=" + requiresImmediateAttention +
                ", machineFacilityName='" + machineFacilityName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}