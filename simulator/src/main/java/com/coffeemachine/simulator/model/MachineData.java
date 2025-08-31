package com.coffeemachine.simulator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "machine_analytics")
@Data
public class MachineData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "machine_id", nullable = false)
	private Integer machineId;

	@Column(name = "facility_id", nullable = false)
	private Integer facilityId;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "temperature")
	private Double temperature;

	@Column(name = "water_level")
	private Double waterLevel;

	@Column(name = "milk_level")
	private Double milkLevel;

	@Column(name = "beans_level")
	private Double beansLevel;

	@Column(name = "sugar_level")
	private Double sugarLevel;

	@Column(name = "brew_type")
	private String brewType;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public MachineData() {
		this.createdAt = LocalDateTime.now();
	}

	public MachineData(Integer machineId, Integer facilityId, String status, Double temperature,
			Double waterLevel, Double milkLevel, Double beansLevel, Double sugarLevel,
			String brewType) {
		this();
		this.machineId = machineId;
		this.facilityId = facilityId;
		this.status = status;
		this.temperature = temperature;
		this.waterLevel = waterLevel;
		this.milkLevel = milkLevel;
		this.beansLevel = beansLevel;
		this.sugarLevel = sugarLevel;
		this.brewType = brewType;
		this.timestamp = LocalDateTime.now();
	}
}
