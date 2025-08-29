package com.coffee.coffeeApp.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardSummaryDto {
    
    // General statistics
    private Long totalFacilities;
    private Long totalMachines;
    private Long activeMachines;
    private Long operationalMachines;
    private Long machinesWithIssues;
    
    // Alert statistics
    private Long totalAlerts;
    private Long criticalAlerts;
    private Long supplyAlerts;
    private Long todayAlerts;
    private Long unresolvedAlerts;
    
    // Usage statistics
    private Long totalUsageToday;
    private Long totalUsageThisWeek;
    private Long totalUsageThisMonth;
    private Double averageUsagePerDay;
    
    // Supply level statistics
    private Double averageWaterLevel;
    private Double averageMilkLevel;
    private Double averageBeansLevel;
    private Long machinesWithLowWater;
    private Long machinesWithLowMilk;
    private Long machinesWithLowBeans;
    
    // Performance metrics
    private Double systemUptime; // Percentage
    private LocalDateTime lastDataUpdate;
    private Integer dataRefreshInterval; // In seconds
    
    // Detailed breakdowns
    private List<FacilityDto> facilitiesWithIssues;
    private List<CoffeeMachineDto> problematicMachines;
    private List<AlertLogDto> recentCriticalAlerts;
    private Map<String, Long> usageByBrewType;
    private Map<String, Long> alertsByType;
    private Map<Integer, Long> usageByHour; // Hourly usage pattern
    
    // Facility-specific data (for facility dashboard)
    private String facilityId;
    private String facilityName;
    private String facilityLocation;
    private List<CoffeeMachineDto> facilityMachines;
    
    // Constructors
    public DashboardSummaryDto() {}
    
    // Factory method for admin dashboard
    public static DashboardSummaryDto createAdminDashboard() {
        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setDataRefreshInterval(60); // 60 seconds for admin
        summary.setLastDataUpdate(LocalDateTime.now());
        return summary;
    }
    
    // Factory method for facility dashboard
    public static DashboardSummaryDto createFacilityDashboard(String facilityId, String facilityName, String facilityLocation) {
        DashboardSummaryDto summary = new DashboardSummaryDto();
        summary.setFacilityId(facilityId);
        summary.setFacilityName(facilityName);
        summary.setFacilityLocation(facilityLocation);
        summary.setDataRefreshInterval(30); // 30 seconds for facility
        summary.setLastDataUpdate(LocalDateTime.now());
        return summary;
    }
    
    // Business logic methods
    public Double getOperationalPercentage() {
        if (totalMachines == null || totalMachines == 0) {
            return 0.0;
        }
        return (operationalMachines != null ? operationalMachines.doubleValue() : 0.0) / totalMachines.doubleValue() * 100.0;
    }
    
    public Boolean hasSupplyIssues() {
        return (machinesWithLowWater != null && machinesWithLowWater > 0) ||
               (machinesWithLowMilk != null && machinesWithLowMilk > 0) ||
               (machinesWithLowBeans != null && machinesWithLowBeans > 0);
    }
    
    public Boolean hasCriticalIssues() {
        return criticalAlerts != null && criticalAlerts > 0;
    }
    
    // Getters and Setters
    public Long getTotalFacilities() {
        return totalFacilities;
    }
    
    public void setTotalFacilities(Long totalFacilities) {
        this.totalFacilities = totalFacilities;
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
    
    public Long getMachinesWithIssues() {
        return machinesWithIssues;
    }
    
    public void setMachinesWithIssues(Long machinesWithIssues) {
        this.machinesWithIssues = machinesWithIssues;
    }
    
    public Long getTotalAlerts() {
        return totalAlerts;
    }
    
    public void setTotalAlerts(Long totalAlerts) {
        this.totalAlerts = totalAlerts;
    }
    
    public Long getCriticalAlerts() {
        return criticalAlerts;
    }
    
    public void setCriticalAlerts(Long criticalAlerts) {
        this.criticalAlerts = criticalAlerts;
    }
    
    public Long getSupplyAlerts() {
        return supplyAlerts;
    }
    
    public void setSupplyAlerts(Long supplyAlerts) {
        this.supplyAlerts = supplyAlerts;
    }
    
    public Long getTodayAlerts() {
        return todayAlerts;
    }
    
    public void setTodayAlerts(Long todayAlerts) {
        this.todayAlerts = todayAlerts;
    }
    
    public Long getUnresolvedAlerts() {
        return unresolvedAlerts;
    }
    
    public void setUnresolvedAlerts(Long unresolvedAlerts) {
        this.unresolvedAlerts = unresolvedAlerts;
    }
    
    public Long getTotalUsageToday() {
        return totalUsageToday;
    }
    
    public void setTotalUsageToday(Long totalUsageToday) {
        this.totalUsageToday = totalUsageToday;
    }
    
    public Long getTotalUsageThisWeek() {
        return totalUsageThisWeek;
    }
    
    public void setTotalUsageThisWeek(Long totalUsageThisWeek) {
        this.totalUsageThisWeek = totalUsageThisWeek;
    }
    
    public Long getTotalUsageThisMonth() {
        return totalUsageThisMonth;
    }
    
    public void setTotalUsageThisMonth(Long totalUsageThisMonth) {
        this.totalUsageThisMonth = totalUsageThisMonth;
    }
    
    public Double getAverageUsagePerDay() {
        return averageUsagePerDay;
    }
    
    public void setAverageUsagePerDay(Double averageUsagePerDay) {
        this.averageUsagePerDay = averageUsagePerDay;
    }
    
    public Double getAverageWaterLevel() {
        return averageWaterLevel;
    }
    
    public void setAverageWaterLevel(Double averageWaterLevel) {
        this.averageWaterLevel = averageWaterLevel;
    }
    
    public Double getAverageMilkLevel() {
        return averageMilkLevel;
    }
    
    public void setAverageMilkLevel(Double averageMilkLevel) {
        this.averageMilkLevel = averageMilkLevel;
    }
    
    public Double getAverageBeansLevel() {
        return averageBeansLevel;
    }
    
    public void setAverageBeansLevel(Double averageBeansLevel) {
        this.averageBeansLevel = averageBeansLevel;
    }
    
    public Long getMachinesWithLowWater() {
        return machinesWithLowWater;
    }
    
    public void setMachinesWithLowWater(Long machinesWithLowWater) {
        this.machinesWithLowWater = machinesWithLowWater;
    }
    
    public Long getMachinesWithLowMilk() {
        return machinesWithLowMilk;
    }
    
    public void setMachinesWithLowMilk(Long machinesWithLowMilk) {
        this.machinesWithLowMilk = machinesWithLowMilk;
    }
    
    public Long getMachinesWithLowBeans() {
        return machinesWithLowBeans;
    }
    
    public void setMachinesWithLowBeans(Long machinesWithLowBeans) {
        this.machinesWithLowBeans = machinesWithLowBeans;
    }
    
    public Double getSystemUptime() {
        return systemUptime;
    }
    
    public void setSystemUptime(Double systemUptime) {
        this.systemUptime = systemUptime;
    }
    
    public LocalDateTime getLastDataUpdate() {
        return lastDataUpdate;
    }
    
    public void setLastDataUpdate(LocalDateTime lastDataUpdate) {
        this.lastDataUpdate = lastDataUpdate;
    }
    
    public Integer getDataRefreshInterval() {
        return dataRefreshInterval;
    }
    
    public void setDataRefreshInterval(Integer dataRefreshInterval) {
        this.dataRefreshInterval = dataRefreshInterval;
    }
    
    public List<FacilityDto> getFacilitiesWithIssues() {
        return facilitiesWithIssues;
    }
    
    public void setFacilitiesWithIssues(List<FacilityDto> facilitiesWithIssues) {
        this.facilitiesWithIssues = facilitiesWithIssues;
    }
    
    public List<CoffeeMachineDto> getProblematicMachines() {
        return problematicMachines;
    }
    
    public void setProblematicMachines(List<CoffeeMachineDto> problematicMachines) {
        this.problematicMachines = problematicMachines;
    }
    
    public List<AlertLogDto> getRecentCriticalAlerts() {
        return recentCriticalAlerts;
    }
    
    public void setRecentCriticalAlerts(List<AlertLogDto> recentCriticalAlerts) {
        this.recentCriticalAlerts = recentCriticalAlerts;
    }
    
    public Map<String, Long> getUsageByBrewType() {
        return usageByBrewType;
    }
    
    public void setUsageByBrewType(Map<String, Long> usageByBrewType) {
        this.usageByBrewType = usageByBrewType;
    }
    
    public Map<String, Long> getAlertsByType() {
        return alertsByType;
    }
    
    public void setAlertsByType(Map<String, Long> alertsByType) {
        this.alertsByType = alertsByType;
    }
    
    public Map<Integer, Long> getUsageByHour() {
        return usageByHour;
    }
    
    public void setUsageByHour(Map<Integer, Long> usageByHour) {
        this.usageByHour = usageByHour;
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
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
    
    public List<CoffeeMachineDto> getFacilityMachines() {
        return facilityMachines;
    }
    
    public void setFacilityMachines(List<CoffeeMachineDto> facilityMachines) {
        this.facilityMachines = facilityMachines;
    }
    
    @Override
    public String toString() {
        return "DashboardSummaryDto{" +
                "totalFacilities=" + totalFacilities +
                ", totalMachines=" + totalMachines +
                ", operationalMachines=" + operationalMachines +
                ", totalAlerts=" + totalAlerts +
                ", criticalAlerts=" + criticalAlerts +
                ", totalUsageToday=" + totalUsageToday +
                ", systemUptime=" + systemUptime +
                ", facilityName='" + facilityName + '\'' +
                ", lastDataUpdate=" + lastDataUpdate +
                '}';
    }
}