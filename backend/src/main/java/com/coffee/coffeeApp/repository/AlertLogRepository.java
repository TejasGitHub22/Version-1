package com.coffee.coffeeApp.repository;

import com.coffee.coffeeApp.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Integer> {
    
    // Find active alerts
    List<AlertLog> findByIsActiveTrueOrderByTimestampDesc();
    
    // Find alerts by machine
    List<AlertLog> findByMachineIdAndIsActiveTrueOrderByTimestampDesc(Integer machineId);
    
    // Find alerts by type
    List<AlertLog> findByAlertTypeAndIsActiveTrueOrderByTimestampDesc(String alertType);
    
    // Find alerts by machine and type
    List<AlertLog> findByMachineIdAndAlertTypeAndIsActiveTrueOrderByTimestampDesc(Integer  machineId, String alertType);
    
    //Find acknowledged alerts
    List<AlertLog> findByIsAcknowledgedTrue();
    
    //Find unacknowledged alerts
    List<AlertLog> findByIsAcknowledgedFalse();
    
    //exists by an 
    
    //Find count of acknowledged alerts
    long countByIsAcknowledgedTrue();
    
    //Find count of unacknowledged alerts
    long countByIsAcknowledgedFalse();
    
    // Find recent alerts (within specified hours)
    @Query("SELECT al FROM AlertLog al WHERE al.timestamp >= :since AND al.isActive = true ORDER BY al.timestamp DESC")
    List<AlertLog> findRecentAlerts(@Param("since") LocalDateTime since);
    
    // Find critical alerts (malfunction, emergency, etc.)
    @Query("SELECT al FROM AlertLog al WHERE al.alertType IN ('MALFUNCTION', 'EMERGENCY', 'OFFLINE') " +
           "AND al.isActive = true ORDER BY al.timestamp DESC")
    List<AlertLog> findCriticalAlerts();
    
    // Find supply alerts (low water, milk, beans)
    @Query("SELECT al FROM AlertLog al WHERE al.alertType IN ('LOW_WATER', 'LOW_MILK', 'LOW_BEANS') " +
           "AND al.isActive = true ORDER BY al.timestamp DESC")
    List<AlertLog> findSupplyAlerts();
    
    // Find alerts within date range
    @Query("SELECT al FROM AlertLog al WHERE al.timestamp BETWEEN :startDate AND :endDate " +
           "AND al.isActive = true ORDER BY al.timestamp DESC")
    List<AlertLog> findAlertsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    // Find today's alerts
    @Query("SELECT al FROM AlertLog al WHERE DATE(al.timestamp) = CURRENT_DATE " +
           "AND al.isActive = true ORDER BY al.timestamp DESC")
    List<AlertLog> findTodayAlerts();
    
    // Find unresolved alerts (recent critical alerts)
    @Query("SELECT al FROM AlertLog al WHERE al.alertType IN ('MALFUNCTION', 'EMERGENCY', 'OFFLINE') " +
           "AND al.timestamp >= :since AND al.isActive = true ORDER BY al.timestamp DESC")
    List<AlertLog> findUnresolvedAlerts(@Param("since") LocalDateTime since);
    
    // Count alerts by type
    @Query("SELECT al.alertType, COUNT(al) FROM AlertLog al WHERE al.isActive = true " +
           "GROUP BY al.alertType ORDER BY COUNT(al) DESC")
    List<Object[]> countAlertsByType();
    
    // Count alerts by machine
    @Query("SELECT al.machineId, COUNT(al) FROM AlertLog al WHERE al.isActive = true " +
           "GROUP BY al.machineId ORDER BY COUNT(al) DESC")
    List<Object[]> countAlertsByMachine();
    
    // Get alert frequency by facility
    @Query("SELECT cm.facilityId, COUNT(al) FROM AlertLog al JOIN al.coffeeMachine cm " +
           "WHERE al.isActive = true GROUP BY cm.facilityId ORDER BY COUNT(al) DESC")
    List<Object[]> getAlertFrequencyByFacility();
    
    // Find machines with frequent alerts (more than threshold in time period)
    @Query("SELECT al.machineId, COUNT(al) FROM AlertLog al " +
           "WHERE al.timestamp >= :since AND al.isActive = true " +
           "GROUP BY al.machineId HAVING COUNT(al) > :threshold ORDER BY COUNT(al) DESC")
    List<Object[]> findMachinesWithFrequentAlerts(@Param("since") LocalDateTime since, 
                                                 @Param("threshold") Long threshold);
    
    // Get alert trends (daily count for last N days)
    @Query("SELECT DATE(al.timestamp) as date, COUNT(al) FROM AlertLog al " +
           "WHERE al.timestamp >= :since AND al.isActive = true " +
           "GROUP BY DATE(al.timestamp) ORDER BY date")
    List<Object[]> getAlertTrends(@Param("since") LocalDateTime since);
    
    // Find peak alert hours
    @Query("SELECT HOUR(al.timestamp) as hour, COUNT(al) as alertCount FROM AlertLog al " +
           "WHERE al.timestamp >= :since AND al.isActive = true " +
           "GROUP BY HOUR(al.timestamp) ORDER BY alertCount DESC")
    List<Object[]> findPeakAlertHours(@Param("since") LocalDateTime since);
    
    // Get most problematic machines (highest alert count)
    @Query("SELECT al.machineId, cm.facilityId, COUNT(al) as alertCount FROM AlertLog al " +
           "JOIN al.coffeeMachine cm WHERE al.timestamp >= :since AND al.isActive = true " +
           "GROUP BY al.machineId, cm.facilityId ORDER BY alertCount DESC")
    List<Object[]> getMostProblematicMachines(@Param("since") LocalDateTime since);
    
    // Find alerts requiring immediate attention
    @Query("SELECT al FROM AlertLog al WHERE al.alertType IN ('MALFUNCTION', 'EMERGENCY') " +
           "AND al.timestamp >= :since AND al.isActive = true ORDER BY al.timestamp DESC")
    List<AlertLog> findAlertsRequiringAttention(@Param("since") LocalDateTime since);
    
    // Count total active alerts
    Long countByIsActiveTrue();
    
    // Count alerts by machine
    Long countByMachineIdAndIsActiveTrue(Integer  machineId);
    
    // Count critical alerts
    @Query("SELECT COUNT(al) FROM AlertLog al WHERE al.alertType IN ('MALFUNCTION', 'EMERGENCY', 'OFFLINE') " +
           "AND al.isActive = true")
    Long countCriticalAlerts();
    
    // Count supply alerts
    @Query("SELECT COUNT(al) FROM AlertLog al WHERE al.alertType IN ('LOW_WATER', 'LOW_MILK', 'LOW_BEANS') " +
           "AND al.isActive = true")
    Long countSupplyAlerts();
    
    // Find duplicate alerts (same machine, same type, within time window)
    @Query("SELECT al FROM AlertLog al WHERE al.machineId = :machineId AND al.alertType = :alertType " +
           "AND al.timestamp BETWEEN :startTime AND :endTime AND al.isActive = true " +
           "ORDER BY al.timestamp DESC")
    List<AlertLog> findDuplicateAlerts(@Param("machineId") Integer machineId, 
                                     @Param("alertType") String alertType,
                                     @Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);
    
    // Get alert summary for dashboard
    @Query("SELECT " +
           "COUNT(CASE WHEN al.alertType IN ('MALFUNCTION', 'EMERGENCY', 'OFFLINE') THEN 1 END) as criticalCount, " +
           "COUNT(CASE WHEN al.alertType IN ('LOW_WATER', 'LOW_MILK', 'LOW_BEANS') THEN 1 END) as supplyCount, " +
           "COUNT(CASE WHEN al.timestamp >= :today THEN 1 END) as todayCount, " +
           "COUNT(al) as totalCount " +
           "FROM AlertLog al WHERE al.isActive = true")
    Object[] getAlertSummary(@Param("today") LocalDateTime today);
    
    // Soft delete alert
    @Query("UPDATE AlertLog al SET al.isActive = false, al.lastUpdate = CURRENT_TIMESTAMP WHERE al.id = :alertId")
    void softDeleteAlert(@Param("alertId") Integer alertId);
    
    // Mark alerts as resolved (soft delete all alerts of specific type for a machine)
    @Query("UPDATE AlertLog al SET al.isActive = false, al.lastUpdate = CURRENT_TIMESTAMP " +
           "WHERE al.machineId = :machineId AND al.alertType = :alertType")
    void resolveAlertsByMachineAndType(@Param("machineId") Integer machineId, @Param("alertType") String alertType);
}