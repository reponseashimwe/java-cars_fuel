package com.example.cars.service;

import java.util.Calendar;
import java.util.List;

import com.example.cars.model.Car;
import com.example.cars.repository.CarRepository;
import com.example.cars.util.ValidationUtils;
import org.springframework.stereotype.Service;

@Service
public class CarService {
    private final CarRepository carRepository;
    private final ValidationUtils validationUtils;

    public CarService(CarRepository carRepository, ValidationUtils validationUtils) {
        this.carRepository = carRepository;
        this.validationUtils = validationUtils;
    }

    // Create a new car
    public Car createCar(Car car) {
        validateYear(car.getYear());
        return carRepository.save(car);
    }

    // Get all cars
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // Get a car by id
    public Car getCarById(Long id) {
        validateIdExists(id);
        return carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));
    }

    // Update a car
    public Car updateCar(Long id, Car car) {
        validateIdExists(id);
        validateYear(car.getYear());
        Car existingCar = getCarById(id);
        existingCar.setBrand(car.getBrand());
        existingCar.setModel(car.getModel());
        existingCar.setYear(car.getYear());
        return carRepository.save(existingCar);
    }

    // Delete a car
    public void deleteCar(Long id) {
        validateIdExists(id);
        carRepository.delete(id);
    }


    private void validateIdExists(Long id) {
        validationUtils.validateEntityExists(id, carRepository::existsById, "Car");
    }

    private void validateYear(Integer year) {
        if (year != null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (year > currentYear) {
                throw new IllegalArgumentException("Year cannot exceed current year (" + currentYear + ")");
            }
        }
    }
}
