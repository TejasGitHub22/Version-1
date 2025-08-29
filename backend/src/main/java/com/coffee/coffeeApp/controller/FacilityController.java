package com.coffee.coffeeApp.controller;

import com.coffee.coffeeApp.dto.FacilityDto;
import com.coffee.coffeeApp.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "*")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@Valid @RequestBody FacilityDto facilityDto) {
        FacilityDto createdFacility = facilityService.createFacility(facilityDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFacility);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable String id) {
        return facilityService.getFacilityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<FacilityDto>> getFacilitiesByLocation(@PathVariable String location) {
        List<FacilityDto> facilities = facilityService.getFacilitiesByLocation(location);
        return ResponseEntity.ok(facilities);
    }

    @GetMapping("/{id}/with-machines")
    public ResponseEntity<FacilityDto> getFacilityWithMachines(@PathVariable String id) {
        return facilityService.getFacilityWithMachines(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FacilityDto> updateFacility(@PathVariable String id, @Valid @RequestBody FacilityDto facilityDto) {
        FacilityDto updatedFacility = facilityService.updateFacility(id, facilityDto);
        return ResponseEntity.ok(updatedFacility);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable String id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reactivate")
    public ResponseEntity<FacilityDto> reactivateFacility(@PathVariable String id) {
        FacilityDto reactivatedFacility = facilityService.reactivateFacility(id);
        return ResponseEntity.ok(reactivatedFacility);
    }
}