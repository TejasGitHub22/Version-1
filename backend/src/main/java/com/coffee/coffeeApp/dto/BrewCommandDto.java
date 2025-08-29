package com.coffee.coffeeApp.dto;

import jakarta.validation.constraints.*;

public class BrewCommandDto {
    
    @NotBlank(message = "Machine ID is required")
    private String machineId;
    
    @NotBlank(message = "Brew type is required")
    @Pattern(regexp = "ESPRESSO|AMERICANO|LATTE|CAPPUCCINO|MACCHIATO|MOCHA|BLACK_COFFEE|CUSTOM", 
             message = "Invalid brew type")
    private String brewType;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    // Customization options
    @DecimalMin(value = "0.5", message = "Size must be at least 0.5")
    @DecimalMax(value = "3.0", message = "Size cannot exceed 3.0")
    private Float size = 1.0f; // Size multiplier (1.0 = normal, 1.5 = large, etc.)
    
    @DecimalMin(value = "0.0", message = "Strength must be between 0 and 2")
    @DecimalMax(value = "2.0", message = "Strength must be between 0 and 2")
    private Float strength = 1.0f; // Strength multiplier (0.5 = mild, 1.0 = normal, 2.0 = strong)
    
    @DecimalMin(value = "0.0", message = "Milk ratio must be between 0 and 1")
    @DecimalMax(value = "1.0", message = "Milk ratio must be between 0 and 1")
    private Float milkRatio = 0.0f; // 0.0 = no milk, 1.0 = maximum milk
    
    @DecimalMin(value = "60.0", message = "Temperature must be at least 60°C")
    @DecimalMax(value = "95.0", message = "Temperature cannot exceed 95°C")
    private Float temperature = 80.0f; // Brewing temperature in Celsius
    
    private Boolean extraHot = false;
    private Boolean extraFoam = false;
    private Boolean decaf = false;
    
    @Size(max = 200, message = "Special instructions cannot exceed 200 characters")
    private String specialInstructions;
    
    // Constructors
    public BrewCommandDto() {}
    
    public BrewCommandDto(String machineId, String brewType, String userId) {
        this.machineId = machineId;
        this.brewType = brewType;
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getMachineId() {
        return machineId;
    }
    
    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
    
    public String getBrewType() {
        return brewType;
    }
    
    public void setBrewType(String brewType) {
        this.brewType = brewType;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Float getSize() {
        return size;
    }
    
    public void setSize(Float size) {
        this.size = size;
    }
    
    public Float getStrength() {
        return strength;
    }
    
    public void setStrength(Float strength) {
        this.strength = strength;
    }
    
    public Float getMilkRatio() {
        return milkRatio;
    }
    
    public void setMilkRatio(Float milkRatio) {
        this.milkRatio = milkRatio;
    }
    
    public Float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }
    
    public Boolean getExtraHot() {
        return extraHot;
    }
    
    public void setExtraHot(Boolean extraHot) {
        this.extraHot = extraHot;
    }
    
    public Boolean getExtraFoam() {
        return extraFoam;
    }
    
    public void setExtraFoam(Boolean extraFoam) {
        this.extraFoam = extraFoam;
    }
    
    public Boolean getDecaf() {
        return decaf;
    }
    
    public void setDecaf(Boolean decaf) {
        this.decaf = decaf;
    }
    
    public String getSpecialInstructions() {
        return specialInstructions;
    }
    
    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
    
    @Override
    public String toString() {
        return "BrewCommandDto{" +
                "machineId='" + machineId + '\'' +
                ", brewType='" + brewType + '\'' +
                ", userId='" + userId + '\'' +
                ", size=" + size +
                ", strength=" + strength +
                ", milkRatio=" + milkRatio +
                ", temperature=" + temperature +
                ", extraHot=" + extraHot +
                ", extraFoam=" + extraFoam +
                ", decaf=" + decaf +
                ", specialInstructions='" + specialInstructions + '\'' +
                '}';
    }
}