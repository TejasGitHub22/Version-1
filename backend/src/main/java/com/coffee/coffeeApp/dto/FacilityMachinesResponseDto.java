package com.coffee.coffeeApp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FacilityMachinesResponseDto {
    private List<CoffeeMachineDto> machines;
    private List<AlertLogDto> alerts;
    private LocalDateTime refreshedAt;

    public FacilityMachinesResponseDto() {
    }

    public FacilityMachinesResponseDto(List<CoffeeMachineDto> machines, List<AlertLogDto> alerts) {
        this.machines = machines;
        this.alerts = alerts;
        this.refreshedAt = LocalDateTime.now();
    }

    public List<CoffeeMachineDto> getMachines() {
        return machines;
    }

    public void setMachines(List<CoffeeMachineDto> machines) {
        this.machines = machines;
    }

    public List<AlertLogDto> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertLogDto> alerts) {
        this.alerts = alerts;
    }

    public LocalDateTime getRefreshedAt() {
        return refreshedAt;
    }

    public void setRefreshedAt(LocalDateTime refreshedAt) {
        this.refreshedAt = refreshedAt;
    }
}


