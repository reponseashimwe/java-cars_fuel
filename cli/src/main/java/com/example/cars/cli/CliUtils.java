package com.example.cars.cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

public class CliUtils {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parses command-line arguments into a map of key-value pairs.
     * Expects arguments in the format: --key value
     */
    public static Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--") && i + 1 < args.length) {
                String key = args[i].substring(2); // Remove "--" prefix
                String value = args[i + 1];
                params.put(key, value);
                i++; // Skip the value in next iteration
            }
        }
        return params;
    }

    /**
     * Gets a parameter value as String, or returns null if not found.
     */
    public static String getStringParam(Map<String, String> params, String key) {
        return params.get(key);
    }

    /**
     * Gets a parameter value as Long, or returns null if not found or invalid.
     */
    public static Long getLongParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets a parameter value as Integer, or returns null if not found or invalid.
     */
    public static Integer getIntegerParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets a parameter value as Double, or returns null if not found or invalid.
     */
    public static Double getDoubleParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Attempts to parse a parameter as Long, but returns the raw string if parsing fails.
     * This allows the API to handle validation errors.
     */
    public static Object getLongOrStringParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return value; // Return raw string, let API validate
        }
    }

    /**
     * Attempts to parse a parameter as Integer, but returns the raw string if parsing fails.
     * This allows the API to handle validation errors.
     */
    public static Object getIntegerOrStringParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value; // Return raw string, let API validate
        }
    }

    /**
     * Attempts to parse a parameter as Double, but returns the raw string if parsing fails.
     * This allows the API to handle validation errors.
     */
    public static Object getDoubleOrStringParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return value; // Return raw string, let API validate
        }
    }

    /**
     * Performs a GET request to the specified URL.
     */
    public static HttpResponse<String> get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Performs a POST request to the specified URL with JSON body.
     */
    public static HttpResponse<String> post(String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .header("Content-Type", "application/json")
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Performs a PUT request to the specified URL with JSON body.
     */
    public static HttpResponse<String> put(String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
            .header("Content-Type", "application/json")
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Performs a DELETE request to the specified URL.
     */
    public static HttpResponse<String> delete(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .DELETE()
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Builds a JSON object from a map of key-value pairs.
     * Uses ObjectMapper for proper JSON serialization.
     */
    public static String buildJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Escapes special characters in JSON strings.
     */
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Prints the response body with pretty-printed JSON if applicable, and handles errors.
     * For error responses, extracts and displays only the error message.
     */
    public static void printResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String body = response.body();
        
        if (statusCode >= 400) {
            // For errors, extract and display just the message
            String errorMessage = extractErrorMessage(body);
            if (errorMessage != null) {
                System.err.println("Error: " + errorMessage);
            } else {
                // Fallback if we can't parse the error response
                System.err.println("Error: HTTP " + statusCode);
                if (body != null && !body.trim().isEmpty()) {
                    System.err.println(body);
                }
            }
        } else {
            // For successful responses, pretty print the JSON
            String formattedBody = prettyPrintJson(body);
            System.out.println(formattedBody);
        }
    }

    /**
     * Extracts the error message from an API error response JSON.
     * Returns null if the message cannot be extracted.
     */
    private static String extractErrorMessage(String jsonBody) {
        if (jsonBody == null || jsonBody.trim().isEmpty()) {
            return null;
        }
        
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonBody);
            // Try to get the "message" field from ErrorResponse
            JsonNode messageNode = jsonNode.get("message");
            if (messageNode != null && messageNode.isTextual()) {
                return messageNode.asText();
            }
        } catch (Exception e) {
            // If parsing fails, return null to use fallback
        }
        
        return null;
    }

    /**
     * Attempts to pretty print a JSON string. Returns the original string if it's not valid JSON.
     */
    private static String prettyPrintJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return jsonString;
        }
        
        try {
            // Parse the JSON
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            // Pretty print with 2-space indentation
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            // If it's not valid JSON, return as-is
            return jsonString;
        }
    }
}

