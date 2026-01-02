package com.example.cars.repository;

import com.example.cars.model.Car;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CarRepository {

    private final Map<Long, Car> storage = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    // CREATE
    public Car save(Car car) {
        if (car.getId() == null) {
            car.setId(nextId.getAndIncrement());
        }
        storage.put(car.getId(), car);
        return car;
    }

    // READ by ID
    public Optional<Car> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    // READ all
    public List<Car> findAll() {
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
}

