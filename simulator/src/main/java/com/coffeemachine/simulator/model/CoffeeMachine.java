package com.coffeemachine.simulator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "coffee_machine")
@Data
public class CoffeeMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "beans_level")
    private Double beansLevel;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "facility_id", nullable = false)
    private Integer facilityId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    @Column(name = "milk_level")
    private Double milkLevel;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "water_level")
    private Double waterLevel;
    
    @Column(name = "sugar_level")
    private Double sugarLevel;
    
    public CoffeeMachine() {
    	
    }

	public CoffeeMachine(Integer id, Double beansLevel, LocalDateTime creationDate, Integer facilityId, Boolean isActive,
			LocalDateTime lastUpdate, Double milkLevel, String status, Double temperature, Double waterLevel, Double sugarLevel) {
		super();
		this.id = id;
		this.beansLevel = beansLevel;
		this.creationDate = creationDate;
		this.facilityId = facilityId;
		this.isActive = isActive;
		this.lastUpdate = lastUpdate;
		this.milkLevel = milkLevel;
		this.status = status;
		this.temperature = temperature;
		this.waterLevel = waterLevel;
		this.sugarLevel = sugarLevel;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getBeansLevel() {
		return beansLevel;
	}

	public void setBeansLevel(Double beansLevel) {
		this.beansLevel = beansLevel;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Double getMilkLevel() {
		return milkLevel;
	}

	public void setMilkLevel(Double milkLevel) {
		this.milkLevel = milkLevel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getWaterLevel() {
		return waterLevel;
	}

	public void setWaterLevel(Double waterLevel) {
		this.waterLevel = waterLevel;
	}

	@Override
	public String toString() {
		return "CoffeeMachine [id=" + id + ", beansLevel=" + beansLevel + ", creationDate=" + creationDate
				+ ", facilityId=" + facilityId + ", isActive=" + isActive + ", lastUpdate=" + lastUpdate
				+ ", milkLevel=" + milkLevel + ", status=" + status + ", temperature=" + temperature + ", waterLevel="
				+ waterLevel +"sugarLevel="+sugarLevel+ "]";
	}
}
