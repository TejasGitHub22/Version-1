package com.coffee.coffeeApp.repository;

import com.coffee.coffeeApp.entity.UsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Integer> {
    
    // Find active usage records
    List<UsageHistory> findByIsActiveTrue();
    
    //Find by usageHistoryId
    Optional<UsageHistory> findById(Integer id);
    
    // Find usage by machine
    List<UsageHistory> findByMachineIdAndIsActiveTrueOrderByTimestampDesc(Integer machineId);
    
    // Find usage by brew type
    List<UsageHistory> findByBrewTypeAndIsActiveTrueOrderByTimestampDesc(String brewType);
    
    // Find usage within date range
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.timestamp BETWEEN :startDate AND :endDate AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findUsageByDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    // Find usage by machine within date range
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.machineId = :machineId AND uh.timestamp BETWEEN :startDate AND :endDate " +
           "AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findUsageByMachineAndDateRange(@Param("machineId") Integer machineId,
                                                    @Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    // Find today's usage
    @Query("SELECT uh FROM UsageHistory uh WHERE DATE(uh.timestamp) = CURRENT_DATE AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findTodayUsage();
    
    // Find today's usage by machine
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.machineId = :machineId AND DATE(uh.timestamp) = CURRENT_DATE " +
           "AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findTodayUsageByMachine(@Param("machineId") Integer machineId);
    
    // Count usage by brew type
    @Query("SELECT uh.brewType, COUNT(uh) FROM UsageHistory uh WHERE uh.isActive = true GROUP BY uh.brewType ORDER BY COUNT(uh) DESC")
    List<Object[]> countUsageByBrewType();
    
    // Count usage by machine
    @Query("SELECT uh.machineId, COUNT(uh) FROM UsageHistory uh WHERE uh.isActive = true GROUP BY uh.machineId ORDER BY COUNT(uh) DESC")
    List<Object[]> countUsageByMachine();
    
    // Get hourly usage statistics
    @Query("SELECT HOUR(uh.timestamp) as hour, COUNT(uh) FROM UsageHistory uh " +
           "WHERE DATE(uh.timestamp) = CURRENT_DATE AND uh.isActive = true GROUP BY HOUR(uh.timestamp) ORDER BY hour")
    List<Object[]> getHourlyUsageStatistics();
    
    // Get daily usage statistics for last 30 days
    @Query("SELECT DATE(uh.timestamp) as date, COUNT(uh) FROM UsageHistory uh " +
           "WHERE uh.timestamp >= :thirtyDaysAgo AND uh.isActive = true GROUP BY DATE(uh.timestamp) ORDER BY date")
    List<Object[]> getDailyUsageStatistics(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    // Get usage statistics by facility (through machine)
    @Query("SELECT cm.facilityId, COUNT(uh), uh.brewType FROM UsageHistory uh " +
           "JOIN uh.coffeeMachine cm WHERE uh.isActive = true " +
           "GROUP BY cm.facilityId, uh.brewType ORDER BY cm.facilityId, COUNT(uh) DESC")
    List<Object[]> getUsageStatisticsByFacility();
    
    // Find peak usage hours
    @Query("SELECT HOUR(uh.timestamp) as hour, COUNT(uh) as usageCount FROM UsageHistory uh " +
           "WHERE uh.timestamp >= :since AND uh.isActive = true " +
           "GROUP BY HOUR(uh.timestamp) ORDER BY usageCount DESC")
    List<Object[]> findPeakUsageHours(@Param("since") LocalDateTime since);
    
    // Get most popular brew types
    @Query("SELECT uh.brewType, COUNT(uh) as usageCount FROM UsageHistory uh " +
           "WHERE uh.timestamp >= :since AND uh.isActive = true " +
           "GROUP BY uh.brewType ORDER BY usageCount DESC")
    List<Object[]> getMostPopularBrewTypes(@Param("since") LocalDateTime since);
    
    // Get usage trends (monthly aggregation)
    @Query("SELECT YEAR(uh.timestamp) as year, MONTH(uh.timestamp) as month, COUNT(uh) as usageCount " +
           "FROM UsageHistory uh WHERE uh.isActive = true " +
           "GROUP BY YEAR(uh.timestamp), MONTH(uh.timestamp) ORDER BY year DESC, month DESC")
    List<Object[]> getUsageTrends();
    
    // Count total usage for a machine
    Long countByMachineIdAndIsActiveTrue(Integer machineId);
    
    // Find recent usage (last N hours)
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.timestamp >= :since AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findRecentUsage(@Param("since") LocalDateTime since);
    
    // Get average usage per day
    @Query("SELECT AVG(dailyCount) FROM (SELECT DATE(uh.timestamp) as date, COUNT(uh) as dailyCount FROM UsageHistory uh " +
           "WHERE uh.isActive = true GROUP BY DATE(uh.timestamp)) as dailyUsage")
    Double getAverageUsagePerDay();
    
    // Soft delete usage record
    @Query("UPDATE UsageHistory uh SET uh.isActive = false, uh.lastUpdate = CURRENT_TIMESTAMP WHERE uh.id = :usageId")
    void softDeleteUsage(@Param("usageId") Integer usageId);
}