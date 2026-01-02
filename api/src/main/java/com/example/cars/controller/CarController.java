package com.example.cars.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.example.cars.dto.*;
import com.example.cars.model.Car;
import com.example.cars.model.FuelEntry;
import com.example.cars.service.CarService;
import com.example.cars.service.FuelEntryService;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;
    private final FuelEntryService fuelEntryService;

    public CarController(CarService carService, FuelEntryService fuelEntryService) {
        this.carService = carService;
        this.fuelEntryService = fuelEntryService;
    }

    // GET all cars
    @GetMapping
    public ResponseEntity<Response<List<Car>>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(Response.success(cars));
    }

    // GET a car by id
    @GetMapping("/{id}")
    public ResponseEntity<Response<Car>> getCarById(@PathVariable("id") Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(Response.success(car));
    }

    // POST a new car
    @PostMapping
    public ResponseEntity<Response<Car>> createCar(@Valid @RequestBody CarRequest request) {
        Car car = new Car(request.getBrand(), request.getModel(), request.getYear());
        Car createdCar = carService.createCar(car);
        return new ResponseEntity<>(Response.success(createdCar, "Car created successfully"), HttpStatus.CREATED);
    }
    
    // PUT an existing car
    @PutMapping("/{id}")
    public ResponseEntity<Response<Car>> updateCar(@PathVariable("id") Long id, @Valid @RequestBody CarRequest request) {
        Car car = new Car(request.getBrand(), request.getModel(), request.getYear());
        Car updatedCar = carService.updateCar(id, car);
        return ResponseEntity.ok(Response.success(updatedCar, "Car updated successfully"));
    }
    
    // DELETE a car
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Object>> deleteCar(@PathVariable("id") Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok(Response.successWithMessage("Car with ID " + id + " has been successfully deleted"));
    }

    // Fill a car with fuel
    @PostMapping("/{id}/fuel")
    public ResponseEntity<Response<FuelEntry>> fillCarWithFuel(@PathVariable("id") Long id, @Valid @RequestBody FuelEntryRequest request) {
        FuelEntry fuelEntry = new FuelEntry(id, request.getLiters(), request.getPrice(), request.getOdometer());
        FuelEntry createdFuelEntry = fuelEntryService.createFuelEntry(fuelEntry);
        return new ResponseEntity<>(Response.success(createdFuelEntry, "Fuel entry created successfully"), HttpStatus.CREATED);
    }

    // GET all fuel entries for a car
    @GetMapping("/{id}/fuel")
    public ResponseEntity<Response<List<FuelEntry>>> getAllFuelEntries(@PathVariable("id") Long id) {
        List<FuelEntry> fuelEntries = fuelEntryService.getAllFuelEntriesByCarId(id);
        return ResponseEntity.ok(Response.success(fuelEntries));
    }

    // GET fuel stats for a car
    @GetMapping("/{id}/fuel/stats")
    public ResponseEntity<Response<FuelStatsResponse>> getFuelStats(@PathVariable("id") Long id) {
        Map<String, Double> stats = fuelEntryService.getFuelStats(id);
        FuelStatsResponse response = new FuelStatsResponse();
        response.totalLiters = stats.get("totalLiters");
        response.totalPrice = stats.get("totalPrice");
        response.avgPer100km = stats.get("avgPer100km");
        return ResponseEntity.ok(Response.success(response));
    }
}
