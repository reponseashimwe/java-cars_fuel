package com.example.cars.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.example.cars.dto.*;
import com.example.cars.model.FuelEntry;
import com.example.cars.service.FuelEntryService;

@RestController
@RequestMapping("/api/fuel-entries")
public class FuelEntryController {
    private final FuelEntryService fuelEntryService;

    public FuelEntryController(FuelEntryService fuelEntryService) {
        this.fuelEntryService = fuelEntryService;
    }

    // GET all fuel entries
    @GetMapping
    public ResponseEntity<Response<List<FuelEntry>>> getAllFuelEntries() {
        List<FuelEntry> fuelEntries = fuelEntryService.getAllFuelEntries();
        return ResponseEntity.ok(Response.success(fuelEntries));
    }

    // GET a fuel entry by id
    @GetMapping("/{id}")
    public ResponseEntity<Response<FuelEntry>> getFuelEntryById(@PathVariable("id") Long id) {
        FuelEntry fuelEntry = fuelEntryService.getFuelEntryById(id);
        return ResponseEntity.ok(Response.success(fuelEntry));
    }

    // PUT an existing fuel entry
    @PutMapping("/{id}")
    public ResponseEntity<Response<FuelEntry>> updateFuelEntry(@PathVariable("id") Long id, @Valid @RequestBody FuelEntryRequest request) {
        FuelEntry fuelEntry = new FuelEntry();
        fuelEntry.setLiters(request.getLiters());
        fuelEntry.setPrice(request.getPrice());
        fuelEntry.setOdometer(request.getOdometer());
        FuelEntry updatedFuelEntry = fuelEntryService.updateFuelEntry(id, fuelEntry);
        return ResponseEntity.ok(Response.success(updatedFuelEntry, "Fuel entry updated successfully"));
    }

    // DELETE a fuel entry
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Object>> deleteFuelEntry(@PathVariable("id") Long id) {
        fuelEntryService.deleteFuelEntry(id);
        return ResponseEntity.ok(Response.successWithMessage("Fuel entry with ID " + id + " has been successfully deleted"));
    }
}
