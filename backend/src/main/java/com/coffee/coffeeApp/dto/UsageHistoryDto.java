package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class UsageHistoryDto {
    
    private String id;
    
    @NotBlank(message = "Machine ID is required")
    private String machineId;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Brew type is required")
    @Pattern(regexp = "ESPRESSO|AMERICANO|LATTE|CAPPUCCINO|MACCHIATO|MOCHA|BLACK_COFFEE|CUSTOM", 
             message = "Invalid brew type")
    private String brewType;
    
    @NotBlank(message = "User is required")
    private String user;
    
    private Boolean isActive;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Additional fields for API responses
    private String machineFacilityId;
    private String machineFacilityName;
    private String machineFacilityLocation;
    private String userName;
    private String userRole;
    
    // Constructors
    public UsageHistoryDto() {}
    
    public UsageHistoryDto(String id, String machineId, String brewType) {
        this.id = id;
        this.machineId = machineId;
        this.brewType = brewType;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Business logic methods
    public Boolean getIsRecentUsage() {
        return timestamp != null && timestamp.isAfter(LocalDateTime.now().minusHours(24));
    }
    
    public Boolean getIsTodayUsage() {
        return timestamp != null && timestamp.toLocalDate().equals(LocalDateTime.now().toLocalDate());
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
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getBrewType() {
        return brewType;
    }
    
    public void setBrewType(String brewType) {
        this.brewType = brewType;
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    @Override
    public String toString() {
        return "UsageHistoryDto{" +
                "id='" + id + '\'' +
                ", machineId='" + machineId + '\'' +
                ", timestamp=" + timestamp +
                ", brewType='" + brewType + '\'' +
                ", user='" + user + '\'' +
                ", userName='" + userName + '\'' +
                ", machineFacilityName='" + machineFacilityName + '\'' +
                ", isActive=" + isActive +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}