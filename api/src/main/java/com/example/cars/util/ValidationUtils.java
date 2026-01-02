package com.example.cars.util;

import java.util.function.Predicate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ValidationUtils {
    
    /**
     * Validates that an ID is not null.
     * 
     * @param id the ID to validate
     * @param entityName the name of the entity (e.g., "Car", "Fuel entry") for error messages
     * @throws ResponseStatusException if ID is null
     */
    public void validateIdNotNull(Long id, String entityName) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                entityName + " ID cannot be null");
        }
    }

    /**
     * Validates that an entity exists using the provided existence checker.
     * 
     * @param id the ID to check
     * @param existsChecker a predicate that checks if the entity exists
     * @param entityName the name of the entity for error messages
     * @throws ResponseStatusException if ID is null or entity not found
     */
    public void validateEntityExists(Long id, Predicate<Long> existsChecker, String entityName) {
        validateIdNotNull(id, entityName);
        if (!existsChecker.test(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                entityName + " with ID " + id + " not found");
        }
    }
}

