package com.coffee.coffeeApp.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoffeeMachineDataDto {
	private Integer machineId;
	private Integer facilityId;
	private String status;
	private float temperature;
	private float waterLevel;
	private float milkLevel;
	private float beansLevel;
	private float sugarLevel;
	private String BrewType;
	private LocalDateTime timeStamp;
	
	
	public Integer getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}
	public Integer getMachineId() {
		return machineId;
	}
	public void setMachineId(Integer machineId) {
		this.machineId = machineId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	public float getWaterLevel() {
		return waterLevel;
	}
	public void setWaterLevel(float waterLevel) {
		this.waterLevel = waterLevel;
	}
	public float getMilkLevel() {
		return milkLevel;
	}
	public void setMilkLevel(float milkLevel) {
		this.milkLevel = milkLevel;
	}
	public float getBeansLevel() {
		return beansLevel;
	}
	public void setBeansLevel(float beansLevel) {
		this.beansLevel = beansLevel;
	}
	public float getSugarLevel() {
		return sugarLevel;
	}
	public void setSugarLevel(Integer sugarLevel) {
		this.sugarLevel = sugarLevel;
	}
	public String getBrewType() {
		return BrewType;
	}
	public void setBrewType(String brewType) {
		BrewType = brewType;
	}
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	// Handle both "timestamp" and "timeStamp" from JSON
	@JsonProperty("timestamp")
	public void setTimestamp(LocalDateTime timestamp) {
		this.timeStamp = timestamp;
	}
	
	public CoffeeMachineDataDto(Integer id, Integer machineId, String status, float temperature, float waterLevel, float milkLevel,
			float beansLevel, float sugarLevel, String brewType, LocalDateTime timeStamp) {
		super();
		this.machineId = machineId;
		this.status = status;
		this.temperature = temperature;
		this.waterLevel = waterLevel;
		this.milkLevel = milkLevel;
		this.beansLevel = beansLevel;
		this.sugarLevel = sugarLevel;
		BrewType = brewType;
		this.timeStamp = timeStamp;
	}
	
	public CoffeeMachineDataDto() {

	}
	
	@Override
	public String toString() {
		return "CoffeeMachineDataDto [machineId=" + machineId + ", facilityId=" + facilityId + ", status=" + status
				+ ", temperature=" + temperature + ", waterLevel=" + waterLevel + ", milkLevel=" + milkLevel
				+ ", beansLevel=" + beansLevel + ", sugarLevel=" + sugarLevel + ", BrewType=" + BrewType
				+ ", timeStamp=" + timeStamp + "]";
	}
	
}
