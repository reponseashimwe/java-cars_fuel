package com.example.cars.repository;

import com.example.cars.model.FuelEntry;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FuelEntryRepository {

    private final Map<Long, FuelEntry> storage = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    // CREATE
    public FuelEntry save(FuelEntry fuelEntry) {
        if (fuelEntry.getId() == null) {
            fuelEntry.setId(nextId.getAndIncrement());
        }
        storage.put(fuelEntry.getId(), fuelEntry);
        return fuelEntry;
    }

    // READ by ID
    public Optional<FuelEntry> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    // READ all
    public List<FuelEntry> findAll() {
        return new ArrayList<>(storage.values());
    }

    // DELETE
    public void delete(Long id) {
        storage.remove(id);
    }

    // EXISTS
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    // FIND by Car ID
    public List<FuelEntry> findByCarId(Long carId) {
        return storage.values().stream()
                .filter(fuelEntry -> fuelEntry.getCarId().equals(carId))
                .collect(Collectors.toList());
    }
}

