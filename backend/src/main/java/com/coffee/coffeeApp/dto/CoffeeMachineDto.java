package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public class CoffeeMachineDto {
    
    private String id;
    
    @NotBlank(message = "Facility ID is required")
    private String facilityId;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ON|OFF", message = "Status must be either ON or OFF")
    private String status;
    
    @DecimalMin(value = "0.0", message = "Temperature must be positive")
    @DecimalMax(value = "200.0", message = "Temperature must be reasonable")
    private Float temperature;
    
    @DecimalMin(value = "0.0", message = "Water level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Water level must be between 0 and 100")
    private Float waterLevel;
    
    @DecimalMin(value = "0.0", message = "Milk level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Milk level must be between 0 and 100")
    private Float milkLevel;
    
    @DecimalMin(value = "0.0", message = "Beans level must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Beans level must be between 0 and 100")
    private Float beansLevel;
    
    private Boolean isActive;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Additional fields for API responses
    private String facilityName;
    private String facilityLocation;
    private Boolean isOperational;
    private Boolean hasLowSupplies;
    private Boolean hasLowWater;
    private Boolean hasLowMilk;
    private Boolean hasLowBeans;
    private Long totalUsageCount;
    private Long todayUsageCount;
    private Long activeAlertCount;
    private List<AlertLogDto> recentAlerts;
    private List<UsageHistoryDto> recentUsage;
    
    // Constructors
    public CoffeeMachineDto() {}
    
    public CoffeeMachineDto(String id, String facilityId, String status) {
        this.id = id;
        this.facilityId = facilityId;
        this.status = status;
        this.isActive = true;
        this.waterLevel = 100.0f;
        this.milkLevel = 100.0f;
        this.beansLevel = 100.0f;
        this.temperature = 0.0f;
    }
    
    // Business logic methods
    public Boolean getIsLowWaterLevel() {
        return waterLevel != null && waterLevel < 20.0f;
    }
    
    public Boolean getIsLowMilkLevel() {
        return milkLevel != null && milkLevel < 20.0f;
    }
    
    public Boolean getIsLowBeansLevel() {
        return beansLevel != null && beansLevel < 20.0f;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }
    
    public Float getWaterLevel() {
        return waterLevel;
    }
    
    public void setWaterLevel(Float waterLevel) {
        this.waterLevel = waterLevel;
    }
    
    public Float getMilkLevel() {
        return milkLevel;
    }
    
    public void setMilkLevel(Float milkLevel) {
        this.milkLevel = milkLevel;
    }
    
    public Float getBeansLevel() {
        return beansLevel;
    }
    
    public void setBeansLevel(Float beansLevel) {
        this.beansLevel = beansLevel;
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
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public String getFacilityLocation() {
        return facilityLocation;
    }
    
    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }
    
    public Boolean getIsOperational() {
        return isOperational;
    }
    
    public void setIsOperational(Boolean isOperational) {
        this.isOperational = isOperational;
    }
    
    public Boolean getHasLowSupplies() {
        return hasLowSupplies;
    }
    
    public void setHasLowSupplies(Boolean hasLowSupplies) {
        this.hasLowSupplies = hasLowSupplies;
    }
    
    public Boolean getHasLowWater() {
        return hasLowWater;
    }
    
    public void setHasLowWater(Boolean hasLowWater) {
        this.hasLowWater = hasLowWater;
    }
    
    public Boolean getHasLowMilk() {
        return hasLowMilk;
    }
    
    public void setHasLowMilk(Boolean hasLowMilk) {
        this.hasLowMilk = hasLowMilk;
    }
    
    public Boolean getHasLowBeans() {
        return hasLowBeans;
    }
    
    public void setHasLowBeans(Boolean hasLowBeans) {
        this.hasLowBeans = hasLowBeans;
    }
    
    public Long getTotalUsageCount() {
        return totalUsageCount;
    }
    
    public void setTotalUsageCount(Long totalUsageCount) {
        this.totalUsageCount = totalUsageCount;
    }
    
    public Long getTodayUsageCount() {
        return todayUsageCount;
    }
    
    public void setTodayUsageCount(Long todayUsageCount) {
        this.todayUsageCount = todayUsageCount;
    }
    
    public Long getActiveAlertCount() {
        return activeAlertCount;
    }
    
    public void setActiveAlertCount(Long activeAlertCount) {
        this.activeAlertCount = activeAlertCount;
    }
    
    public List<AlertLogDto> getRecentAlerts() {
        return recentAlerts;
    }
    
    public void setRecentAlerts(List<AlertLogDto> recentAlerts) {
        this.recentAlerts = recentAlerts;
    }
    
    public List<UsageHistoryDto> getRecentUsage() {
        return recentUsage;
    }
    
    public void setRecentUsage(List<UsageHistoryDto> recentUsage) {
        this.recentUsage = recentUsage;
    }
    
    @Override
    public String toString() {
        return "CoffeeMachineDto{" +
                "id='" + id + '\'' +
                ", facilityId='" + facilityId + '\'' +
                ", status='" + status + '\'' +
                ", temperature=" + temperature +
                ", waterLevel=" + waterLevel +
                ", milkLevel=" + milkLevel +
                ", beansLevel=" + beansLevel +
                ", isActive=" + isActive +
                ", isOperational=" + isOperational +
                ", hasLowSupplies=" + hasLowSupplies +
                ", totalUsageCount=" + totalUsageCount +
                ", activeAlertCount=" + activeAlertCount +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}