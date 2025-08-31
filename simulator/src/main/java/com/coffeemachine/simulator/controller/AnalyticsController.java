package com.coffeemachine.simulator.controller;

import com.coffeemachine.simulator.model.MachineData;
import com.coffeemachine.simulator.repository.MachineDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private MachineDataRepository machineDataRepository;

    // Get all machine data
    @GetMapping("/machines")
    public ResponseEntity<List<MachineData>> getAllMachineData() {
        List<MachineData> data = machineDataRepository.findAll();
        return ResponseEntity.ok(data);
    }

    // Get machine data by machine ID
    @GetMapping("/machines/{machineId}")
    public ResponseEntity<List<MachineData>> getMachineDataById(@PathVariable Integer machineId) {
        List<MachineData> data = machineDataRepository.findByMachineIdOrderByTimestampDesc(machineId);
        return ResponseEntity.ok(data);
    }

    // Get machine data by date range
    @GetMapping("/machines/date-range")
    public ResponseEntity<List<MachineData>> getMachineDataByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<MachineData> data = machineDataRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // Get usage statistics by brew type
    @GetMapping("/usage/brew-types")
    public ResponseEntity<Map<String, Long>> getBrewTypeUsage() {
        List<MachineData> allData = machineDataRepository.findAll();
        Map<String, Long> brewTypeCounts = allData.stream()
                .filter(data -> data.getBrewType() != null && !"None".equals(data.getBrewType()))
                .collect(Collectors.groupingBy(
                        MachineData::getBrewType,
                        Collectors.counting()));
        return ResponseEntity.ok(brewTypeCounts);
    }

    // Get machine status distribution
    @GetMapping("/status/distribution")
    public ResponseEntity<Map<String, Long>> getStatusDistribution() {
        List<MachineData> allData = machineDataRepository.findAll();
        Map<String, Long> statusCounts = allData.stream()
                .collect(Collectors.groupingBy(
                        MachineData::getStatus,
                        Collectors.counting()));
        return ResponseEntity.ok(statusCounts);
    }

    // Get average resource levels
    @GetMapping("/resources/averages")
    public ResponseEntity<Map<String, Double>> getAverageResourceLevels() {
        List<MachineData> allData = machineDataRepository.findAll();

        double avgWater = allData.stream()
                .mapToDouble(data -> data.getWaterLevel() != null ? data.getWaterLevel() : 0.0)
                .average()
                .orElse(0.0);

        double avgMilk = allData.stream()
                .mapToDouble(data -> data.getMilkLevel() != null ? data.getMilkLevel() : 0.0)
                .average()
                .orElse(0.0);

        double avgBeans = allData.stream()
                .mapToDouble(data -> data.getBeansLevel() != null ? data.getBeansLevel() : 0.0)
                .average()
                .orElse(0.0);

        double avgSugar = allData.stream()
                .mapToDouble(data -> data.getSugarLevel() != null ? data.getSugarLevel() : 0.0)
                .average()
                .orElse(0.0);

        double avgTemp = allData.stream()
                .mapToDouble(data -> data.getTemperature() != null ? data.getTemperature() : 0.0)
                .average()
                .orElse(0.0);

        Map<String, Double> averages = Map.of(
                "waterLevel", avgWater,
                "milkLevel", avgMilk,
                "beansLevel", avgBeans,
                "sugarLevel", avgSugar,
                "temperature", avgTemp);

        return ResponseEntity.ok(averages);
    }

    // Get recent activity (last 24 hours)
    @GetMapping("/recent-activity")
    public ResponseEntity<List<MachineData>> getRecentActivity() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<MachineData> recentData = machineDataRepository
                .findByTimestampAfterOrderByTimestampDesc(twentyFourHoursAgo);
        return ResponseEntity.ok(recentData);
    }
}
