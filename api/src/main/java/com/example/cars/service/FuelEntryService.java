package com.example.cars.service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import com.example.cars.model.FuelEntry;
import com.example.cars.repository.FuelEntryRepository;
import com.example.cars.util.ValidationUtils;
import org.springframework.stereotype.Service;

@Service
public class FuelEntryService {
    private final FuelEntryRepository fuelEntryRepository;
    private final CarService carService;
    private final ValidationUtils validationUtils;

    public FuelEntryService(FuelEntryRepository fuelEntryRepository, CarService carService, ValidationUtils validationUtils) {
        this.fuelEntryRepository = fuelEntryRepository;
        this.carService = carService;
        this.validationUtils = validationUtils;
    }

    // Create a new fuel entry
    public FuelEntry createFuelEntry(FuelEntry fuelEntry) {
        // Validate that the car exists
        validateCarIdExists(fuelEntry.getCarId());
        // Validate odometer doesn't decrease
        validateOdometerNotDecreasing(fuelEntry.getCarId(), fuelEntry.getOdometer());
        // Set timestamp if not already set
        if (fuelEntry.getTimestamp() == null) {
            fuelEntry.setTimestamp(LocalDateTime.now());
        }
        return fuelEntryRepository.save(fuelEntry);
    }

    // Get all fuel entries
    public List<FuelEntry> getAllFuelEntries() {
        return fuelEntryRepository.findAll();
    }

    // Get a fuel entry by id
    public FuelEntry getFuelEntryById(Long id) {
        validateIdExists(id);
        return fuelEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fuel entry not found with id: " + id));
    }
    
    // Update a fuel entry
    public FuelEntry updateFuelEntry(Long id, FuelEntry fuelEntry) {
        validateIdExists(id);
        FuelEntry existingFuelEntry = getFuelEntryById(id);
        // Validate odometer against previous and next entries (by timestamp)
        validateOdometerForUpdate(existingFuelEntry.getCarId(), existingFuelEntry.getId(), existingFuelEntry.getTimestamp(), fuelEntry.getOdometer());
        existingFuelEntry.setLiters(fuelEntry.getLiters());
        existingFuelEntry.setPrice(fuelEntry.getPrice());
        existingFuelEntry.setOdometer(fuelEntry.getOdometer());

        return fuelEntryRepository.save(existingFuelEntry);
    }

    // Delete a fuel entry
    public void deleteFuelEntry(Long id) {
        validateIdExists(id);
        fuelEntryRepository.delete(id);
    }

    // Fuel Stats
    public Map<String, Double> getFuelStats(Long carId) {
        validateCarIdExists(carId);
        List<FuelEntry> fuelEntries = getAllFuelEntriesByCarId(carId);
        if (fuelEntries.isEmpty()) {
            // Return zeros if no fuel entries exist
            Map<String, Double> emptyStats = new HashMap<>();
            emptyStats.put("totalLiters", 0.0);
            emptyStats.put("totalPrice", 0.0);
            emptyStats.put("avgPer100km", 0.0);
            return emptyStats;
        }
        return calculateFuelStats(fuelEntries);
    }   

    // Calculate fuel stats
    // Formula: Average (L/100km) = (Total fuel used / Total distance driven) × 100
    // Distance = last odometer reading - first odometer reading
    private Map<String, Double> calculateFuelStats(List<FuelEntry> fuelEntries) {
        Map<String, Double> stats = new HashMap<>();
        
        // Sum all fuel used across all entries
        double totalLiters = fuelEntries.stream().mapToDouble(FuelEntry::getLiters).sum();
        double totalPrice = fuelEntries.stream().mapToDouble(FuelEntry::getTotalPrice).sum();
        
        stats.put("totalLiters", totalLiters);
        stats.put("totalPrice", totalPrice);
        
        // Calculate total distance driven: max odometer - min odometer
        // Sort entries by odometer to find min and max values
        List<FuelEntry> sortedEntries = fuelEntries.stream()
            .sorted(Comparator.comparingInt(FuelEntry::getOdometer))
            .collect(Collectors.toList());
        
        int minOdometer = sortedEntries.get(0).getOdometer();
        int maxOdometer = sortedEntries.get(sortedEntries.size() - 1).getOdometer();
        
        // If there's only one entry or entries have same odometer, assume previous was 0
        // Otherwise, calculate distance from min to max odometer
        int totalDistance;
        if (sortedEntries.size() == 1 || minOdometer == maxOdometer) {
            // No previous odometer available, assume car started at 0
            totalDistance = maxOdometer;
        } else {
            totalDistance = maxOdometer - minOdometer;
        }
        
        // Calculate average fuel consumption per 100km
        // Formula: (Total fuel used / Total distance driven) × 100
        double avgPer100km = 0.0;
        if (totalDistance > 0) {
            avgPer100km = (totalLiters / totalDistance) * 100.0;
            // Round to 2 decimal places
            avgPer100km = Math.round(avgPer100km * 100.0) / 100.0;
        }
        
        stats.put("avgPer100km", avgPer100km);
        
        return stats;
    }

    // Get all fuel entries by car id
    public List<FuelEntry> getAllFuelEntriesByCarId(Long carId) {
        validateCarIdExists(carId);
        return fuelEntryRepository.findByCarId(carId);
    }


    private void validateIdExists(Long id) {
        validationUtils.validateEntityExists(id, fuelEntryRepository::existsById, "Fuel entry");
    }

    private void validateCarIdExists(Long carId) {
        validationUtils.validateIdNotNull(carId, "Car");
        // Validate car exists by trying to get it
        carService.getCarById(carId);
    }

    private void validateOdometerNotDecreasing(Long carId, int newOdometer) {
        List<FuelEntry> existingEntries = getAllFuelEntriesByCarId(carId);
        if (!existingEntries.isEmpty()) {
            int maxOdometer = existingEntries.stream()
                    .mapToInt(FuelEntry::getOdometer)
                    .max()
                    .orElse(0);
            if (newOdometer < maxOdometer) {
                throw new IllegalArgumentException("Odometer cannot decrease. Maximum odometer for this car: " + maxOdometer + ", New: " + newOdometer);
            }
        }
    }

    private void validateOdometerForUpdate(Long carId, Long entryId, LocalDateTime entryTimestamp, int newOdometer) {
        List<FuelEntry> allEntries = getAllFuelEntriesByCarId(carId);
        // Sort by timestamp
        List<FuelEntry> sortedEntries = allEntries.stream()
                .sorted(Comparator.comparing(FuelEntry::getTimestamp))
                .collect(Collectors.toList());
        
        // Find the entry being updated
        int currentIndex = -1;
        for (int i = 0; i < sortedEntries.size(); i++) {
            if (sortedEntries.get(i).getId().equals(entryId)) {
                currentIndex = i;
                break;
            }
        }
        
        if (currentIndex == -1) {
            return; // Entry not found in list, skip validation
        }
        
        // Check previous entry (before this one by timestamp)
        if (currentIndex > 0) {
            FuelEntry previousEntry = sortedEntries.get(currentIndex - 1);
            if (newOdometer < previousEntry.getOdometer()) {
                throw new IllegalArgumentException("Odometer cannot be below previous entry. Previous odometer: " + previousEntry.getOdometer() + ", New: " + newOdometer);
            }
        }
        
        // Check next entry (after this one by timestamp)
        if (currentIndex < sortedEntries.size() - 1) {
            FuelEntry nextEntry = sortedEntries.get(currentIndex + 1);
            if (newOdometer > nextEntry.getOdometer()) {
                throw new IllegalArgumentException("Odometer cannot be above next entry. Next odometer: " + nextEntry.getOdometer() + ", New: " + newOdometer);
            }
        }
    }
}
