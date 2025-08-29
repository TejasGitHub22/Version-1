package com.coffee.coffeeApp.service;

import com.coffee.coffeeApp.dto.UsageHistoryDto;
import com.coffee.coffeeApp.entity.UsageHistory;
import com.coffee.coffeeApp.repository.UsageHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsageHistoryService {
    
    @Autowired
    private UsageHistoryRepository usageHistoryRepository;
    
    // Create usage record
    public UsageHistoryDto createUsageRecord(String machineId, String brewType) {
        UsageHistory usage = new UsageHistory(Integer.parseInt(machineId), brewType);
        UsageHistory savedUsage = usageHistoryRepository.save(usage);
        return convertToDto(savedUsage);
    }
    
    // Create usage record with custom timestamp
    public UsageHistoryDto createUsageRecord(UsageHistoryDto usageDto) {
        if (usageDto.getId() == null || usageDto.getId().isEmpty()) {
            usageDto.setId(UUID.randomUUID().toString());
        }
        
        UsageHistory usage = convertToEntity(usageDto);
        UsageHistory savedUsage = usageHistoryRepository.save(usage);
        return convertToDto(savedUsage);
    }
    
    // Get usage by ID
    @Transactional(readOnly = true)
    public Optional<UsageHistoryDto> getUsageById(String id) {
        return usageHistoryRepository.findById(Integer.parseInt(id))
                .filter(UsageHistory::getIsActive)
                .map(this::convertToDto);
    }
    
    // Get all usage records
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getAllUsage() {
        return usageHistoryRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get usage by machine
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getUsageByMachine(String machineId) {
        return usageHistoryRepository.findByMachineIdAndIsActiveTrueOrderByTimestampDesc(Integer.parseInt(machineId))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
//    // Get usage by user
//    @Transactional(readOnly = true)
//    public List<UsageHistoryDto> getUsageByUser(String userId) {
//        return usageHistoryRepository.findByUserAndIsActiveTrueOrderByTimestampDesc(Integer.parseInt(userId))
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
    
    // Get usage by brew type
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getUsageByBrewType(String brewType) {
        return usageHistoryRepository.findByBrewTypeAndIsActiveTrueOrderByTimestampDesc(brewType)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get usage within date range
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getUsageByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return usageHistoryRepository.findUsageByDateRange(startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get usage by machine within date range
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getUsageByMachineAndDateRange(String machineId, LocalDateTime startDate, LocalDateTime endDate) {
        return usageHistoryRepository.findUsageByMachineAndDateRange(Integer.parseInt(machineId), startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get today's usage
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getTodayUsage() {
        return usageHistoryRepository.findTodayUsage()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get today's usage by machine
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getTodayUsageByMachine(String machineId) {
        return usageHistoryRepository.findTodayUsageByMachine(Integer.parseInt(machineId))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get recent usage
    @Transactional(readOnly = true)
    public List<UsageHistoryDto> getRecentUsage(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return usageHistoryRepository.findRecentUsage(since)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get usage statistics
    @Transactional(readOnly = true)
    public UsageStatistics getUsageStatistics() {
        long totalUsage = usageHistoryRepository.findByIsActiveTrue().size();
        long todayUsage = usageHistoryRepository.findTodayUsage().size();
        Double averageUsagePerDay = usageHistoryRepository.getAverageUsagePerDay();
        
        return new UsageStatistics(totalUsage, todayUsage, averageUsagePerDay != null ? averageUsagePerDay : 0.0);
    }
    
    // Get usage count by brew type
    @Transactional(readOnly = true)
    public List<BrewTypeStats> getUsageByBrewTypeStats() {
        List<Object[]> stats = usageHistoryRepository.countUsageByBrewType();
        return stats.stream()
                .map(row -> new BrewTypeStats(
                    (String) row[0], // brewType
                    ((Number) row[1]).longValue() // count
                ))
                .collect(Collectors.toList());
    }
    
    // Get usage count by machine
    @Transactional(readOnly = true)
    public List<MachineUsageStats> getUsageByMachineStats() {
        List<Object[]> stats = usageHistoryRepository.countUsageByMachine();
        return stats.stream()
                .map(row -> new MachineUsageStats(
                    (String) row[0], // machineId
                    ((Number) row[1]).longValue() // count
                ))
                .collect(Collectors.toList());
    }
    
//    // Get usage count by user
//    @Transactional(readOnly = true)
//    public List<UserUsageStats> getUsageByUserStats() {
//        List<Object[]> stats = usageHistoryRepository.countUsageByUser();
//        return stats.stream()
//                .map(row -> new UserUsageStats(
//                    (String) row[0], // userId
//                    ((Number) row[1]).longValue() // count
//                ))
//                .collect(Collectors.toList());
//    }
    
    // Get hourly usage statistics
    @Transactional(readOnly = true)
    public List<HourlyUsageStats> getHourlyUsageStatistics() {
        List<Object[]> stats = usageHistoryRepository.getHourlyUsageStatistics();
        return stats.stream()
                .map(row -> new HourlyUsageStats(
                    ((Number) row[0]).intValue(), // hour
                    ((Number) row[1]).longValue() // count
                ))
                .collect(Collectors.toList());
    }
    
    // Get daily usage statistics for last 30 days
    @Transactional(readOnly = true)
    public List<DailyUsageStats> getDailyUsageStatistics() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Object[]> stats = usageHistoryRepository.getDailyUsageStatistics(thirtyDaysAgo);
        return stats.stream()
                .map(row -> new DailyUsageStats(
                    (java.sql.Date) row[0], // date
                    ((Number) row[1]).longValue() // count
                ))
                .collect(Collectors.toList());
    }
    
    // Get peak usage hours
    @Transactional(readOnly = true)
    public List<HourlyUsageStats> getPeakUsageHours(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> stats = usageHistoryRepository.findPeakUsageHours(since);
        return stats.stream()
                .map(row -> new HourlyUsageStats(
                    ((Number) row[0]).intValue(), // hour
                    ((Number) row[1]).longValue() // count
                ))
                .collect(Collectors.toList());
    }
    
    // Get most popular brew types
    @Transactional(readOnly = true)
    public List<BrewTypeStats> getMostPopularBrewTypes(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> stats = usageHistoryRepository.getMostPopularBrewTypes(since);
        return stats.stream()
                .map(row -> new BrewTypeStats(
                    (String) row[0], // brewType
                    ((Number) row[1]).longValue() // count
                ))
                .collect(Collectors.toList());
    }
    
//    // Get heavy users (top users by usage count)
//    @Transactional(readOnly = true)
//    public List<UserUsageStats> getHeavyUsers(int days) {
//        LocalDateTime since = LocalDateTime.now().minusDays(days);
//        List<Object[]> stats = usageHistoryRepository.findHeavyUsers(since);
//        return stats.stream()
//                .map(row -> new UserUsageStats(
//                    (String) row[0], // userId
//                    ((Number) row[1]).longValue() // count
//                ))
//                .collect(Collectors.toList());
//    }
    
    // Get usage trends (monthly aggregation)
    @Transactional(readOnly = true)
    public List<MonthlyUsageStats> getUsageTrends() {
        List<Object[]> trends = usageHistoryRepository.getUsageTrends();
        return trends.stream()
                .map(row -> new MonthlyUsageStats(
                    ((Number) row[0]).intValue(), // year
                    ((Number) row[1]).intValue(), // month
                    ((Number) row[2]).longValue() // count
                ))
                .collect(Collectors.toList());
    }
    
    // Get usage count for machine
    @Transactional(readOnly = true)
    public Long getUsageCountByMachine(String machineId) {
        return usageHistoryRepository.countByMachineIdAndIsActiveTrue(Integer.parseInt(machineId));
    }
    
    // Get usage count for user
//    @Transactional(readOnly = true)
//    public Long getUsageCountByUser(String userId) {
//        return usageHistoryRepository.countByUserAndIsActiveTrue(Integer.parseInt(userId));
//    }
    
    // Soft delete usage record
    public void deleteUsageRecord(String usageId) {
        UsageHistory usage = usageHistoryRepository.findById(Integer.parseInt(usageId))
                .filter(UsageHistory::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Usage record not found: " + usageId));
        
        usage.setIsActive(false);
        usageHistoryRepository.save(usage);
    }
    
    // Conversion methods
    private UsageHistoryDto convertToDto(UsageHistory usage) {
        UsageHistoryDto dto = new UsageHistoryDto();
        dto.setId(String.valueOf(usage.getId()));
        dto.setMachineId(String.valueOf(usage.getMachineId()));
        dto.setTimestamp(usage.getTimestamp());
        dto.setBrewType(usage.getBrewType());
        dto.setIsActive(usage.getIsActive());
        dto.setCreationDate(usage.getCreationDate());
        dto.setLastUpdate(usage.getLastUpdate());
        return dto;
    }
    
    private UsageHistory convertToEntity(UsageHistoryDto dto) {
        UsageHistory usage = new UsageHistory();
        usage.setId(Integer.parseInt(dto.getId()));
        usage.setMachineId(Integer.parseInt(dto.getMachineId()));
        usage.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        usage.setBrewType(dto.getBrewType());
        usage.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return usage;
    }
    
    // Inner classes for statistics
    public static class UsageStatistics {
        private final long totalUsage;
        private final long todayUsage;
        private final double averageUsagePerDay;
        
        public UsageStatistics(long totalUsage, long todayUsage, double averageUsagePerDay) {
            this.totalUsage = totalUsage;
            this.todayUsage = todayUsage;
            this.averageUsagePerDay = averageUsagePerDay;
        }
        
        public long getTotalUsage() { return totalUsage; }
        public long getTodayUsage() { return todayUsage; }
        public double getAverageUsagePerDay() { return averageUsagePerDay; }
    }
    
    public static class BrewTypeStats {
        private final String brewType;
        private final long count;
        
        public BrewTypeStats(String brewType, long count) {
            this.brewType = brewType;
            this.count = count;
        }
        
        public String getBrewType() { return brewType; }
        public long getCount() { return count; }
    }
    
    public static class MachineUsageStats {
        private final String machineId;
        private final long count;
        
        public MachineUsageStats(String machineId, long count) {
            this.machineId = machineId;
            this.count = count;
        }
        
        public String getMachineId() { return machineId; }
        public long getCount() { return count; }
    }
    
    public static class UserUsageStats {
        private final String userId;
        private final long count;
        
        public UserUsageStats(String userId, long count) {
            this.userId = userId;
            this.count = count;
        }
        
        public String getUserId() { return userId; }
        public long getCount() { return count; }
    }
    
    public static class HourlyUsageStats {
        private final int hour;
        private final long count;
        
        public HourlyUsageStats(int hour, long count) {
            this.hour = hour;
            this.count = count;
        }
        
        public int getHour() { return hour; }
        public long getCount() { return count; }
    }
    
    public static class DailyUsageStats {
        private final java.sql.Date date;
        private final long count;
        
        public DailyUsageStats(java.sql.Date date, long count) {
            this.date = date;
            this.count = count;
        }
        
        public java.sql.Date getDate() { return date; }
        public long getCount() { return count; }
    }
    
    public static class MonthlyUsageStats {
        private final int year;
        private final int month;
        private final long count;
        
        public MonthlyUsageStats(int year, int month, long count) {
            this.year = year;
            this.month = month;
            this.count = count;
        }
        
        public int getYear() { return year; }
        public int getMonth() { return month; }
        public long getCount() { return count; }
    }
}