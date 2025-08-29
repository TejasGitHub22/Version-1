package com.coffee.coffeeApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "UsageHistory")
public class UsageHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @NotBlank(message = "Machine ID is required")
    @Column(name = "machineId", nullable = false)
    private Integer machineId;
    
    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Brew type is required")
    @Column(name = "brewType", nullable = false)
    private String brewType; // ESPRESSO, AMERICANO, LATTE, CAPPUCCINO, etc.
    
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
    
    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", referencedColumnName = "id", insertable = false, updatable = false)
    private User userEntity;
    
    // Constructors
    public UsageHistory() {}
    
    public UsageHistory(Integer machineId, String brewType) {
        this.machineId = machineId;
        this.brewType = brewType;
        this.timestamp = LocalDateTime.now();
        this.isActive = true;
    }
    
    // Business Logic Methods
    public boolean isRecentUsage(int hoursAgo) {
        return timestamp != null && timestamp.isAfter(LocalDateTime.now().minusHours(hoursAgo));
    }
    
    public boolean isTodayUsage() {
        return timestamp != null && timestamp.toLocalDate().equals(LocalDateTime.now().toLocalDate());
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
    
    public CoffeeMachine getCoffeeMachine() {
        return coffeeMachine;
    }
    
    public void setCoffeeMachine(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }
    
    public User getUserEntity() {
        return userEntity;
    }
    
    public void setUserEntity(User userEntity) {
        this.userEntity = userEntity;
    }
    
    @Override
    public String toString() {
        return "UsageHistory{" +
                "id='" + id + '\'' +
                ", machineId='" + machineId + '\'' +
                ", timestamp=" + timestamp +
                ", brewType='" + brewType + '\'' +
                ", isActive=" + isActive +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}