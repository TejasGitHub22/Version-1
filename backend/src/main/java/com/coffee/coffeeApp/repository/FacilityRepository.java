package com.coffee.coffeeApp.repository;

import com.coffee.coffeeApp.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Integer> {
    
    // Find active facilities
    List<Facility> findByIsActiveTrue();
    
    // Find facility by name
    Optional<Facility> findByNameAndIsActiveTrue(String name);
    
    // Find facilities by location
    List<Facility> findByLocationAndIsActiveTrue(String location);
    
    // Find facilities by location containing keyword
    List<Facility> findByLocationContainingIgnoreCaseAndIsActiveTrue(String locationKeyword);
    
    // Check if facility name exists
    boolean existsByNameAndIsActiveTrue(String name);
    
    // Find facilities with coffee machines
    @Query("SELECT DISTINCT f FROM Facility f JOIN f.coffeeMachines cm WHERE f.isActive = true AND cm.isActive = true")
    List<Facility> findFacilitiesWithActiveMachines();
    
    // Find facilities with low supply machines
    @Query("SELECT DISTINCT f FROM Facility f JOIN f.coffeeMachines cm WHERE f.isActive = true AND cm.isActive = true " +
           "AND (cm.waterLevel < 20.0 OR cm.milkLevel < 20.0 OR cm.beansLevel < 20.0)")
    List<Facility> findFacilitiesWithLowSupplyMachines();
    
    // Count active machines per facility
    @Query("SELECT f.id, f.name, COUNT(cm) FROM Facility f LEFT JOIN f.coffeeMachines cm " +
           "WHERE f.isActive = true AND (cm.isActive = true OR cm.isActive IS NULL) " +
           "GROUP BY f.id, f.name")
    List<Object[]> countActiveMachinesPerFacility();
    
    // Find facilities created within date range
    @Query("SELECT f FROM Facility f WHERE f.creationDate BETWEEN :startDate AND :endDate AND f.isActive = true")
    List<Facility> findFacilitiesByCreationDateRange(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    // Find recently updated facilities
    @Query("SELECT f FROM Facility f WHERE f.lastUpdate >= :since AND f.isActive = true ORDER BY f.lastUpdate DESC")
    List<Facility> findRecentlyUpdatedFacilities(@Param("since") LocalDateTime since);
    
    // Get facility statistics
    @Query("SELECT f.id, f.name, f.location, COUNT(cm), " +
           "COUNT(CASE WHEN cm.status = 'ON' THEN 1 END), " +
           "COUNT(CASE WHEN cm.waterLevel < 20.0 OR cm.milkLevel < 20.0 OR cm.beansLevel < 20.0 THEN 1 END) " +
           "FROM Facility f LEFT JOIN f.coffeeMachines cm " +
           "WHERE f.isActive = true AND (cm.isActive = true OR cm.isActive IS NULL) " +
           "GROUP BY f.id, f.name, f.location")
    List<Object[]> getFacilityStatistics();
    
    // Find facilities by machine count range
    @Query("SELECT f FROM Facility f WHERE f.isActive = true AND " +
           "(SELECT COUNT(cm) FROM CoffeeMachine cm WHERE cm.facilityId = f.id AND cm.isActive = true) " +
           "BETWEEN :minCount AND :maxCount")
    List<Facility> findFacilitiesByMachineCountRange(@Param("minCount") Long minCount, 
                                                   @Param("maxCount") Long maxCount);
    
    // Soft delete facility
    @Query("UPDATE Facility f SET f.isActive = false, f.lastUpdate = CURRENT_TIMESTAMP WHERE f.id = :facilityId")
    void softDeleteFacility(@Param("facilityId") Integer facilityId);
    
    // Reactivate facility
    @Query("UPDATE Facility f SET f.isActive = true, f.lastUpdate = CURRENT_TIMESTAMP WHERE f.id = :facilityId")
    void reactivateFacility(@Param("facilityId") Integer facilityId);
}