package com.coffee.coffeeApp.repository;

import com.coffee.coffeeApp.entity.CoffeeMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoffeeMachineRepository extends JpaRepository<CoffeeMachine, Integer> {
    
    // Find active machines
    List<CoffeeMachine> findByIsActiveTrue();
    
    // Find machines by facility
    List<CoffeeMachine> findByFacilityIdAndIsActiveTrue(Integer facilityId);
    
    // Find machines by status
    List<CoffeeMachine> findByStatusAndIsActiveTrue(String status);
    
    // Find machines by facility and status
    List<CoffeeMachine> findByFacilityIdAndStatusAndIsActiveTrue(Integer facilityId, String status);
    
    // Find machines with low water level
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.waterLevel < :threshold AND cm.isActive = true")
    List<CoffeeMachine> findMachinesWithLowWater(@Param("threshold") Float threshold);
    
    // Find machines with low milk level
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.milkLevel < :threshold AND cm.isActive = true")
    List<CoffeeMachine> findMachinesWithLowMilk(@Param("threshold") Float threshold);
    
    // Find machines with low beans level
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.beansLevel < :threshold AND cm.isActive = true")
    List<CoffeeMachine> findMachinesWithLowBeans(@Param("threshold") Float threshold);
    
    // Find machines with any low supplies (water, milk, or beans < 20%)
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.isActive = true AND " +
           "(cm.waterLevel < 20.0 OR cm.milkLevel < 20.0 OR cm.beansLevel < 20.0)")
    List<CoffeeMachine> findMachinesWithLowSupplies();
    
    // Find machines with critical supplies (< 10%)
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.isActive = true AND " +
           "(cm.waterLevel < 10.0 OR cm.milkLevel < 10.0 OR cm.beansLevel < 10.0)")
    List<CoffeeMachine> findMachinesWithCriticalSupplies();
    
    // Find operational machines (ON status and no low supplies)
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.isActive = true AND cm.status = 'ON' AND " +
           "cm.waterLevel >= 20.0 AND cm.milkLevel >= 20.0 AND cm.beansLevel >= 20.0")
    List<CoffeeMachine> findOperationalMachines();
    
    // Find machines by temperature range
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.temperature BETWEEN :minTemp AND :maxTemp AND cm.isActive = true")
    List<CoffeeMachine> findMachinesByTemperatureRange(@Param("minTemp") Float minTemp, 
                                                      @Param("maxTemp") Float maxTemp);
    
    // Get machine status summary by facility
    @Query("SELECT cm.facilityId, COUNT(cm), " +
           "COUNT(CASE WHEN cm.status = 'ON' THEN 1 END) as onCount, " +
           "COUNT(CASE WHEN cm.status = 'OFF' THEN 1 END) as offCount, " +
           "COUNT(CASE WHEN cm.waterLevel < 20.0 OR cm.milkLevel < 20.0 OR cm.beansLevel < 20.0 THEN 1 END) as lowSupplyCount " +
           "FROM CoffeeMachine cm WHERE cm.isActive = true GROUP BY cm.facilityId")
    List<Object[]> getMachineStatusSummaryByFacility();
    
    // Find recently updated machines
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.lastUpdate >= :since AND cm.isActive = true ORDER BY cm.lastUpdate DESC")
    List<CoffeeMachine> findRecentlyUpdatedMachines(@Param("since") LocalDateTime since);
    
    // Count machines by facility
    Long countByFacilityIdAndIsActiveTrue(Integer facilityId);
    
    // Count operational machines by facility
    @Query("SELECT COUNT(cm) FROM CoffeeMachine cm WHERE cm.facilityId = :facilityId AND cm.isActive = true AND " +
           "cm.status = 'ON' AND cm.waterLevel >= 20.0 AND cm.milkLevel >= 20.0 AND cm.beansLevel >= 20.0")
    Long countOperationalMachinesByFacility(@Param("facilityId") Integer facilityId);
    
    // Find machines needing maintenance (offline or with alerts)
    @Query("SELECT DISTINCT cm FROM CoffeeMachine cm LEFT JOIN cm.alertLogs al " +
           "WHERE cm.isActive = true AND (cm.status = 'OFF' OR " +
           "(al.isActive = true AND al.alertType IN ('MALFUNCTION', 'EMERGENCY') AND al.timestamp >= :since))")
    List<CoffeeMachine> findMachinesNeedingMaintenance(@Param("since") LocalDateTime since);
    
    // Get average levels by facility
    @Query("SELECT cm.facilityId, AVG(cm.waterLevel), AVG(cm.milkLevel), AVG(cm.beansLevel), AVG(cm.temperature) " +
           "FROM CoffeeMachine cm WHERE cm.isActive = true GROUP BY cm.facilityId")
    List<Object[]> getAverageLevelsByFacility();
    
    // Update machine levels
    @Query("UPDATE CoffeeMachine cm SET cm.waterLevel = :waterLevel, cm.milkLevel = :milkLevel, " +
           "cm.beansLevel = :beansLevel, cm.temperature = :temperature, cm.lastUpdate = CURRENT_TIMESTAMP " +
           "WHERE cm.id = :machineId")
    void updateMachineLevels(@Param("machineId") Integer machineId, 
                           @Param("waterLevel") Float waterLevel,
                           @Param("milkLevel") Float milkLevel, 
                           @Param("beansLevel") Float beansLevel,
                           @Param("temperature") Float temperature);
    
    // Update machine status
    @Query("UPDATE CoffeeMachine cm SET cm.status = :status, cm.lastUpdate = CURRENT_TIMESTAMP WHERE cm.id = :machineId")
    void updateMachineStatus(@Param("machineId") Integer machineId, @Param("status") String status);
    
    // Soft delete machine
    @Query("UPDATE CoffeeMachine cm SET cm.isActive = false, cm.lastUpdate = CURRENT_TIMESTAMP WHERE cm.id = :machineId")
    void softDeleteMachine(@Param("machineId") Integer machineId);
    
    // Reactivate machine
    @Query("UPDATE CoffeeMachine cm SET cm.isActive = true, cm.lastUpdate = CURRENT_TIMESTAMP WHERE cm.id = :machineId")
    void reactivateMachine(@Param("machineId") Integer machineId);
}