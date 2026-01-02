package com.example.cars.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class FuelEntryRequest {
    
    @NotNull(message = "Liters is required")
    @Positive(message = "Liters must be positive")
    private Double liters;
    
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;
    
    @NotNull(message = "Odometer is required")
    @Positive(message = "Odometer must be positive")
    private Integer odometer;
    
    public FuelEntryRequest() {
    }
    
    public FuelEntryRequest(Double liters, Double price, Integer odometer) {
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
    }
    
    public Double getLiters() {
        return liters;
    }
    
    public void setLiters(Double liters) {
        this.liters = liters;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getOdometer() {
        return odometer;
    }
    
    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }
}

