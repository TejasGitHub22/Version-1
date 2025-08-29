package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "AlertLog")
public class AlertLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @NotBlank(message = "Machine ID is required")
    @Column(name = "machineId", nullable = false)
    private Integer machineId;
    
    @NotBlank(message = "Alert type is required")
    @Column(name = "alertType", nullable = false)
    private String alertType; // LOW_WATER, LOW_MILK, LOW_BEANS, MALFUNCTION, etc.
    
    @NotBlank(message = "Message is required")
    @Column(name = "message", nullable = false, length = 500)
    private String message;
    
    @NotNull(message = "Acknowledge info is required")
    @Column(nullable = false)
    private boolean isAcknowledged = false; 
    
    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @NotNull(message = "Active status is required")
    @Column(name = "isActive", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "creationDate", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @UpdateTimestamp
    @Column(name = "lastUpdate", nullable = false)
    private LocalDateTime lastUpdate;
    
    // Many-to-One relationship with CoffeeMachine
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machineId", referencedColumnName = "id", insertable = false, updatable = false)
    private CoffeeMachine coffeeMachine;
    
    // Constructors
    public AlertLog() {}
    
    public AlertLog(Integer machineId, String alertType, String message) {
        this.machineId = machineId;
        this.alertType = alertType;
        this.message = message;
        this.isAcknowledged = false;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Business Logic Methods
    public boolean isCriticalAlert() {
        return "MALFUNCTION".equalsIgnoreCase(alertType) || 
               "OFFLINE".equalsIgnoreCase(alertType) ||
               "EMERGENCY".equalsIgnoreCase(alertType);
    }
    
    public boolean isSupplyAlert() {
        return "LOW_WATER".equalsIgnoreCase(alertType) || 
               "LOW_MILK".equalsIgnoreCase(alertType) ||
               "LOW_BEANS".equalsIgnoreCase(alertType);
    }
    
    public boolean isRecentAlert(int hoursAgo) {
        return timestamp != null && timestamp.isAfter(LocalDateTime.now().minusHours(hoursAgo));
    }
    
    public static AlertLog createLowWaterAlert(Integer machineId, float currentLevel) {
        String message = String.format("Water level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLog(machineId, "LOW_WATER", message);
    }
    
    public static AlertLog createLowMilkAlert(Integer machineId, float currentLevel) {
        String message = String.format("Milk level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLog(machineId, "LOW_MILK", message);
    }
    
    public static AlertLog createLowBeansAlert(Integer machineId, float currentLevel) {
        String message = String.format("Coffee beans level is critically low: %.1f%%. Please refill immediately.", currentLevel);
        return new AlertLog(machineId, "LOW_BEANS", message);
    }
    
    public static AlertLog createMalfunctionAlert(Integer machineId, String issue) {
        String message = String.format("Machine malfunction detected: %s. Maintenance required.", issue);
        return new AlertLog(machineId, "MALFUNCTION", message);
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getMachineId() {
        return machineId;
    }
    
    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean getIsAcknowledged() {
    	return isAcknowledged;
    }
    
    public void setIsAcknowledged(boolean isAcknowledged) {
    	this.isAcknowledged = isAcknowledged;
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
    
    public CoffeeMachine getCoffeeMachine() {
        return coffeeMachine;
    }
    
    public void setCoffeeMachine(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }
    
    @Override
    public String toString() {
        return "AlertLog{" +
                "id='" + id + '\'' +
                ", machineId='" + machineId + '\'' +
                ", alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", isActive=" + isActive +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}