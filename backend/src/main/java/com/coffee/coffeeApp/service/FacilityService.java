package com.coffee.coffeeApp.service;

import com.coffee.coffeeApp.dto.FacilityDto;
import com.coffee.coffeeApp.dto.CoffeeMachineDto;
import com.coffee.coffeeApp.entity.Facility;
import com.coffee.coffeeApp.entity.CoffeeMachine;
import com.coffee.coffeeApp.repository.FacilityRepository;
import com.coffee.coffeeApp.repository.CoffeeMachineRepository;
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
public class FacilityService {
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    @Autowired
    private CoffeeMachineRepository coffeeMachineRepository;
    
    // Removed circular dependency - will use repository directly
    
    // Create new facility
    public FacilityDto createFacility(FacilityDto facilityDto) {
        validateFacilityDto(facilityDto);
        
        // Check if facility name already exists
        if (facilityRepository.existsByNameAndIsActiveTrue(facilityDto.getName())) {
            throw new IllegalArgumentException("Facility name already exists: " + facilityDto.getName());
        }
        
//        // Generate ID if not provided
//        if (facilityDto.getId() == null || facilityDto.getId().isEmpty()) {
//            facilityDto.setId(UUID.randomUUID().toString());
//        }
        
        Facility facility = convertToEntity(facilityDto);
        facility.setIsActive(true);
        
        Facility savedFacility = facilityRepository.save(facility);
        return convertToDto(savedFacility);
    }
    
    // Get facility by ID
    @Transactional(readOnly = true)
    public Optional<FacilityDto> getFacilityById(String id) {
        return facilityRepository.findById(Integer.parseInt(id))
                .filter(Facility::getIsActive)
                .map(this::convertToDtoWithStats);
    }
    
    // Get facility by name
    @Transactional(readOnly = true)
    public Optional<FacilityDto> getFacilityByName(String name) {
        return facilityRepository.findByNameAndIsActiveTrue(name)
                .map(this::convertToDtoWithStats);
    }
    
    // Get all active facilities
    @Transactional(readOnly = true)
    public List<FacilityDto> getAllFacilities() {
        return facilityRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get facilities by location
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesByLocation(String location) {
        return facilityRepository.findByLocationAndIsActiveTrue(location)
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Search facilities by location keyword
    @Transactional(readOnly = true)
    public List<FacilityDto> searchFacilitiesByLocation(String locationKeyword) {
        return facilityRepository.findByLocationContainingIgnoreCaseAndIsActiveTrue(locationKeyword)
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get facilities with active machines
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesWithActiveMachines() {
        return facilityRepository.findFacilitiesWithActiveMachines()
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get facilities with low supply machines
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesWithLowSupplyMachines() {
        return facilityRepository.findFacilitiesWithLowSupplyMachines()
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get facility with machines
    @Transactional(readOnly = true)
    public Optional<FacilityDto> getFacilityWithMachines(String facilityId) {
        Optional<FacilityDto> facilityOpt = getFacilityById(facilityId);
        if (facilityOpt.isPresent()) {
            FacilityDto facility = facilityOpt.get();
            // Get machines directly from repository to avoid circular dependency
            List<CoffeeMachineDto> machines = coffeeMachineRepository.findByFacilityIdAndIsActiveTrue(Integer.parseInt(facilityId))
                    .stream()
                    .map(this::convertCoffeeMachineToDto)
                    .collect(Collectors.toList());
            facility.setMachines(machines);
            return Optional.of(facility);
        }
        return Optional.empty();
    }
    
    // Update facility
    public FacilityDto updateFacility(String id, FacilityDto facilityDto) {
        Facility existingFacility = facilityRepository.findById(Integer.parseInt(id))
                .filter(Facility::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + id));
        
        validateFacilityDto(facilityDto);
        
        // Check if name is being changed and if new name exists
        if (!existingFacility.getName().equals(facilityDto.getName()) &&
            facilityRepository.existsByNameAndIsActiveTrue(facilityDto.getName())) {
            throw new IllegalArgumentException("Facility name already exists: " + facilityDto.getName());
        }
        
        // Update fields
        existingFacility.setName(facilityDto.getName());
        existingFacility.setLocation(facilityDto.getLocation());
        
        Facility savedFacility = facilityRepository.save(existingFacility);
        return convertToDtoWithStats(savedFacility);
    }
    
    // Soft delete facility
    public void deleteFacility(String id) {
        Facility facility = facilityRepository.findById(Integer.parseInt(id))
                .filter(Facility::getIsActive)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + id));
        
        // Check if facility has active machines
        long activeMachines = coffeeMachineRepository.countByFacilityIdAndIsActiveTrue(Integer.parseInt(id));
        if (activeMachines > 0) {
            throw new IllegalStateException("Cannot delete facility with active machines. Please deactivate machines first.");
        }
        
        facility.setIsActive(false);
        facilityRepository.save(facility);
    }
    
    // Reactivate facility
    public FacilityDto reactivateFacility(String id) {
        Facility facility = facilityRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + id));
        
        facility.setIsActive(true);
        Facility savedFacility = facilityRepository.save(facility);
        return convertToDtoWithStats(savedFacility);
    }
    
    // Check if facility exists
    @Transactional(readOnly = true)
    public boolean facilityExists(String id) {
        return facilityRepository.findById(Integer.parseInt(id))
                .map(Facility::getIsActive)
                .orElse(false);
    }
    
    // Check if facility name exists
    @Transactional(readOnly = true)
    public boolean facilityNameExists(String name) {
        return facilityRepository.existsByNameAndIsActiveTrue(name);
    }
    
    // Get facility statistics
    @Transactional(readOnly = true)
    public FacilityStatistics getFacilityStatistics() {
        List<Facility> allFacilities = facilityRepository.findByIsActiveTrue();
        long totalFacilities = allFacilities.size();
        long facilitiesWithMachines = facilityRepository.findFacilitiesWithActiveMachines().size();
        long facilitiesWithIssues = facilityRepository.findFacilitiesWithLowSupplyMachines().size();
        
        return new FacilityStatistics(totalFacilities, facilitiesWithMachines, facilitiesWithIssues);
    }
    
    // Get recently created facilities
    @Transactional(readOnly = true)
    public List<FacilityDto> getRecentlyCreatedFacilities(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return facilityRepository.findFacilitiesByCreationDateRange(since, LocalDateTime.now())
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get facilities by machine count range
    @Transactional(readOnly = true)
    public List<FacilityDto> getFacilitiesByMachineCountRange(Long minCount, Long maxCount) {
        return facilityRepository.findFacilitiesByMachineCountRange(minCount, maxCount)
                .stream()
                .map(this::convertToDtoWithStats)
                .collect(Collectors.toList());
    }
    
    // Get detailed facility statistics
    @Transactional(readOnly = true)
    public List<DetailedFacilityStats> getDetailedFacilityStatistics() {
        List<Object[]> stats = facilityRepository.getFacilityStatistics();
        return stats.stream()
                .map(row -> new DetailedFacilityStats(
                    (String) row[0], // facilityId
                    (String) row[1], // facilityName
                    (String) row[2], // location
                    ((Number) row[3]).longValue(), // totalMachines
                    ((Number) row[4]).longValue(), // activeMachines
                    ((Number) row[5]).longValue()  // lowSupplyMachines
                ))
                .collect(Collectors.toList());
    }
    
    // Validation methods
    private void validateFacilityDto(FacilityDto facilityDto) {
        if (facilityDto == null) {
            throw new IllegalArgumentException("Facility data cannot be null");
        }
        if (facilityDto.getName() == null || facilityDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Facility name is required");
        }
        if (facilityDto.getLocation() == null || facilityDto.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
    }
    
    // Conversion methods
    private FacilityDto convertToDto(Facility facility) {
        FacilityDto dto = new FacilityDto();
        dto.setId(String.valueOf(facility.getId()));
        dto.setName(facility.getName());
        dto.setLocation(facility.getLocation());
        dto.setIsActive(facility.getIsActive());
        dto.setCreationDate(facility.getCreationDate());
        dto.setLastUpdate(facility.getLastUpdate());
        return dto;
    }
    
    private FacilityDto convertToDtoWithStats(Facility facility) {
        FacilityDto dto = convertToDto(facility);
        
        // Add machine statistics
        long totalMachines = coffeeMachineRepository.countByFacilityIdAndIsActiveTrue(facility.getId());
        long operationalMachines = coffeeMachineRepository.countOperationalMachinesByFacility(facility.getId());
        long lowSupplyMachines = coffeeMachineRepository.findMachinesWithLowSupplies()
                .stream()
                .mapToLong(machine -> machine.getFacilityId().equals(facility.getId()) ? 1 : 0)
                .sum();
        
        dto.setTotalMachines(totalMachines);
        dto.setActiveMachines(totalMachines); // All counted machines are active
        dto.setOperationalMachines(operationalMachines);
        dto.setMachinesWithLowSupplies(lowSupplyMachines);
        
        return dto;
    }
    
    private Facility convertToEntity(FacilityDto dto) {
        Facility facility = new Facility();
        facility.setId(Integer.parseInt(dto.getId()));
        facility.setName(dto.getName());
        facility.setLocation(dto.getLocation());
        facility.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return facility;
    }
    
    // Helper method to convert CoffeeMachine entity to DTO
    private CoffeeMachineDto convertCoffeeMachineToDto(CoffeeMachine machine) {
        CoffeeMachineDto dto = new CoffeeMachineDto();
        dto.setId(String.valueOf(machine.getId()));
        dto.setFacilityId(String.valueOf(machine.getFacilityId()));
        dto.setStatus(machine.getStatus());
        dto.setTemperature(machine.getTemperature());
        dto.setWaterLevel(machine.getWaterLevel());
        dto.setMilkLevel(machine.getMilkLevel());
        dto.setBeansLevel(machine.getBeansLevel());
        dto.setIsActive(machine.getIsActive());
        dto.setCreationDate(machine.getCreationDate());
        dto.setLastUpdate(machine.getLastUpdate());
        return dto;
    }
    
    // Inner classes for statistics
    public static class FacilityStatistics {
        private final long totalFacilities;
        private final long facilitiesWithMachines;
        private final long facilitiesWithIssues;
        
        public FacilityStatistics(long totalFacilities, long facilitiesWithMachines, long facilitiesWithIssues) {
            this.totalFacilities = totalFacilities;
            this.facilitiesWithMachines = facilitiesWithMachines;
            this.facilitiesWithIssues = facilitiesWithIssues;
        }
        
        // Getters
        public long getTotalFacilities() { return totalFacilities; }
        public long getFacilitiesWithMachines() { return facilitiesWithMachines; }
        public long getFacilitiesWithIssues() { return facilitiesWithIssues; }
    }
    
    public static class DetailedFacilityStats {
        private final String facilityId;
        private final String facilityName;
        private final String location;
        private final long totalMachines;
        private final long activeMachines;
        private final long lowSupplyMachines;
        
        public DetailedFacilityStats(String facilityId, String facilityName, String location,
                                   long totalMachines, long activeMachines, long lowSupplyMachines) {
            this.facilityId = facilityId;
            this.facilityName = facilityName;
            this.location = location;
            this.totalMachines = totalMachines;
            this.activeMachines = activeMachines;
            this.lowSupplyMachines = lowSupplyMachines;
        }
        
        // Getters
        public String getFacilityId() { return facilityId; }
        public String getFacilityName() { return facilityName; }
        public String getLocation() { return location; }
        public long getTotalMachines() { return totalMachines; }
        public long getActiveMachines() { return activeMachines; }
        public long getLowSupplyMachines() { return lowSupplyMachines; }
    }
}