# Cars Management System

A multi-module Maven project for managing cars and fuel entries, built as part of a backend technical assignment.

**Tech Stack:** Java 17, Spring Boot, Maven
**Storage:** In-memory only (no external database)

## Project Structure

```
cars/
├── pom.xml                 # Parent POM
├── api/                    # Spring Boot REST API Backend
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/       # Backend code (controllers, services, models)
│       │   └── resources/  # application.properties
│       └── test/           # Unit tests
└── cli/                    # Standalone CLI Client
    ├── pom.xml
    └── src/
        └── main/
            └── java/       # CLI code
```

## Modules

### api

Spring Boot REST API backend providing:

* Car management endpoints (`/api/cars`)
* Fuel entry endpoints (`/api/fuel-entries`)
* Car-specific fuel endpoints (`/api/cars/{id}/fuel`)
* Manual Java Servlet endpoint (`/servlet/fuel-stats?carId={id}`) demonstrating the Java Servlet request lifecycle

#### Servlet Endpoint

The servlet is implemented manually (without Spring MVC) and demonstrates:

* Query parameter parsing
* Content-Type configuration
* HTTP status code handling
* Manual JSON response writing

Example request:

```bash
curl "http://localhost:8080/servlet/fuel-stats?carId=1"
```

### cli

Standalone Java CLI application that communicates with the backend API strictly over HTTP.

**Prerequisites:**

* Backend API must be running
* Java 17+

#### Example Commands

```bash
# List all cars
java -jar cli/target/cli-0.0.1-SNAPSHOT.jar list-cars

# Create a car
java -jar cli/target/cli-0.0.1-SNAPSHOT.jar create-car --brand Toyota --model Camry --year 2020

# Add fuel entry
java -jar cli/target/cli-0.0.1-SNAPSHOT.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer 15000

# Get fuel statistics for a car
java -jar cli/target/cli-0.0.1-SNAPSHOT.jar fuel-stats --carId 1
```

#### Available Commands

**Car Commands:**

* `list-cars`
* `get-car --carId <id>`
* `create-car --brand <brand> --model <model> --year <year>`
* `update-car --carId <id> --brand <brand> --model <model> --year <year>`
* `delete-car --carId <id>`

**Fuel Entry Commands (Car-specific):**

* `add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>`
* `fuel-stats --carId <id>`
* `car-fuels --carId <id>`

**Fuel Entry Commands (General):**

* `list-fuel-entries`
* `get-fuel-entry --id <id>`
* `update-fuel-entry --id <id> --carId <id> --liters <liters> --price <price> --odometer <odometer>`
* `delete-fuel-entry --id <id>`

## Error Handling & Validation

* The CLI validates that all required parameters are provided
* Parameter value validation is handled by the API
* The API returns clear, user-friendly error messages
* The CLI displays errors extracted from API responses

Example:

```bash
java -jar cli/target/cli-0.0.1-SNAPSHOT.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer "yes"
# Error: Invalid value 'yes' for field 'odometer'. Expected int.
```

## Building the Project

Build all modules:

```bash
mvn clean install
```

Build a specific module:

```bash
cd api
mvn clean package
```

## Architecture Overview

* **Backend (api):** Spring Boot application with REST controllers, services, and in-memory data storage
* **CLI (cli):** Standalone Java application making HTTP requests to the backend
* **Separation of concerns:** Backend and CLI are independent Maven modules communicating strictly over HTTP
* **Validation strategy:** Required parameter checks in CLI, value validation in API
* **Storage:** In-memory only; data is reset on application restart

## Requirements

* Java 17+
* Maven 3.6+
