package com.example.cars.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success", "message", "data"})
public class Response<T> {
    private boolean success;
    private String message;
    private T data;

    public Response() {
    }

    public Response(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // Factory method for successful responses with data only
    public static <T> Response<T> success(T data) {
        return new Response<>(true, data, null);
    }

    // Factory method for successful responses with data and message
    public static <T> Response<T> success(T data, String message) {
        return new Response<>(true, data, message);
    }

    // Factory method for successful responses with message only (no data)
    public static <T> Response<T> successWithMessage(String message) {
        return new Response<>(true, null, message);
    }

    // Factory method for error responses
    public static <T> Response<T> error(String message) {
        return new Response<>(false, null, message);
    }

    // Factory method for error responses with data (e.g., validation errors)
    public static <T> Response<T> error(String message, T data) {
        return new Response<>(false, data, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


