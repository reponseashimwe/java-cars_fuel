package com.example.cars.exception;

import com.example.cars.dto.Response;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response<Object>> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return new ResponseEntity<>(Response.error(message), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        List<String> errorMessages = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(fieldName + ": " + errorMessage);
        });
        
        String message = "Validation failed: " + String.join(", ", errorMessages);
        return new ResponseEntity<>(
            Response.error(message), 
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        String errorMessage = "Invalid request body format";
        
        // Check for InvalidFormatException in the cause chain
        Throwable cause = ex.getCause();
        InvalidFormatException ife = null;
        while (cause != null) {
            if (cause instanceof InvalidFormatException) {
                ife = (InvalidFormatException) cause;
                break;
            }
            cause = cause.getCause();
        }
        
        if (ife != null) {
            String value = ife.getValue() != null ? ife.getValue().toString() : "null";
            String type = ife.getTargetType() != null 
                ? ife.getTargetType().getSimpleName() : "number";
            
            errorMessage = String.format("Invalid value '%s'. Expected %s.", value, type);
        } else if (ex.getMessage() != null) {
            // Fallback: extract info from message
            String msg = ex.getMessage();
            // Look for pattern: "String \"199s\"" or "not a valid"
            if (msg.contains("String \"")) {
                int start = msg.indexOf("String \"") + 8;
                int end = msg.indexOf("\"", start);
                if (end > start) {
                    String value = msg.substring(start, end);
                    errorMessage = String.format("Invalid value '%s'. Expected a valid number.", value);
                }
            } else if (msg.contains("not a valid")) {
                errorMessage = "Invalid value format. Expected a valid number.";
            }
        }

        return new ResponseEntity<>(
            Response.error(errorMessage), 
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        String parameterName = ex.getName();
        String providedValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String requiredType = ex.getRequiredType() != null 
            ? ex.getRequiredType().getSimpleName() : "number";
        
        String errorMessage = String.format(
            "Invalid value '%s' for parameter '%s'. Expected %s.", 
            providedValue, 
            parameterName, 
            requiredType
        );

        return new ResponseEntity<>(
            Response.error(errorMessage), 
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        String message = ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred";
        return new ResponseEntity<>(Response.error(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

