package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class FacilityDto {
    
    private String id;
    
    @NotBlank(message = "Facility name is required")
    private String name;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private Boolean isActive;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdate;
    
    // Additional fields for API responses
    private Long totalMachines;
    private Long activeMachines;
    private Long operationalMachines;
    private Long machinesWithLowSupplies;
    private List<CoffeeMachineDto> machines;
    
    // Constructors
    public FacilityDto() {}
    
    public FacilityDto(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
    
    public Long getTotalMachines() {
        return totalMachines;
    }
    
    public void setTotalMachines(Long totalMachines) {
        this.totalMachines = totalMachines;
    }
    
    public Long getActiveMachines() {
        return activeMachines;
    }
    
    public void setActiveMachines(Long activeMachines) {
        this.activeMachines = activeMachines;
    }
    
    public Long getOperationalMachines() {
        return operationalMachines;
    }
    
    public void setOperationalMachines(Long operationalMachines) {
        this.operationalMachines = operationalMachines;
    }
    
    public Long getMachinesWithLowSupplies() {
        return machinesWithLowSupplies;
    }
    
    public void setMachinesWithLowSupplies(Long machinesWithLowSupplies) {
        this.machinesWithLowSupplies = machinesWithLowSupplies;
    }
    
    public List<CoffeeMachineDto> getMachines() {
        return machines;
    }
    
    public void setMachines(List<CoffeeMachineDto> machines) {
        this.machines = machines;
    }
    
    @Override
    public String toString() {
        return "FacilityDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", isActive=" + isActive +
                ", totalMachines=" + totalMachines +
                ", activeMachines=" + activeMachines +
                ", operationalMachines=" + operationalMachines +
                ", machinesWithLowSupplies=" + machinesWithLowSupplies +
                ", creationDate=" + creationDate +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}