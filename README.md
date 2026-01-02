# Cars Management System

A multi-module Maven project for managing cars and fuel entries.

## Project Structure

```
cars/
├── pom.xml                 # Parent POM
├── api/                    # Spring Boot REST API Backend
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/      # Backend code (controllers, services, models)
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

-   Car management endpoints (`/api/cars`)
-   Fuel entry endpoints (`/api/fuel-entries`)
-   Car-specific fuel endpoints (`/api/cars/{id}/fuel`)
-   Manual Java Servlet endpoint (`/servlet/fuel-stats?carId={id}`) - demonstrates request lifecycle handling

**To run:**

```bash
cd api
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

**Servlet Endpoint:**

The servlet endpoint demonstrates manual Java Servlet implementation:

```bash
# Get fuel statistics via servlet
curl "http://localhost:8080/servlet/fuel-stats?carId=1"
```

The servlet manually handles:

-   Query parameter parsing
-   Content-Type setting
-   HTTP status codes
-   JSON response writing

### cli

Standalone CLI client that communicates with the REST API via HTTP.

**Prerequisites:**

-   The API server must be running (see api module above)
-   Java 17+ must be installed

**To build:**

```bash
# From the project root
mvn clean package

# Or from the cli directory
cd cli
mvn clean package
```

**To run:**

```bash
# From the project root
java -jar cli/target/cli-0.0.1-SNAPSHOT.jar <command> [arguments]

# Or from the cli directory (after building)
cd cli
java -jar target/cli-0.0.1-SNAPSHOT.jar <command> [arguments]
```

**Examples:**

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

**Error Handling:**

The CLI validates that all required parameters are provided. Parameter value validation (e.g., ensuring numbers are valid) is handled by the API, which returns clear, user-friendly error messages. For example:

```bash
# If you provide an invalid value, the API will return a clear error message
java -jar cli/target/cli-0.0.1-SNAPSHOT.jar add-fuel --carId 1 --liters 40 --price 52.5 --odometer "yes"
# Error: Invalid value 'yes' for field 'odometer'. Expected int.
```

**Available Commands:**

**Car Commands:**

-   `list-cars`
-   `get-car --carId <id>`
-   `create-car --brand <brand> --model <model> --year <year>`
-   `update-car --carId <id> --brand <brand> --model <model> --year <year>`
-   `delete-car --carId <id>`

**Fuel Entry Commands (Car-specific):**

-   `add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>`
-   `fuel-stats --carId <id>`
-   `car-fuels --carId <id>`

**Fuel Entry Commands (General):**

-   `list-fuel-entries`
-   `get-fuel-entry --id <id>`
-   `update-fuel-entry --id <id> --carId <id> --liters <liters> --price <price> --odometer <odometer>`
-   `delete-fuel-entry --id <id>`

## Building the Project

**Build all modules:**

```bash
mvn clean install
```

**Build specific module:**

```bash
cd api
mvn clean package
```

## Requirements

-   Java 17+
-   Maven 3.6+

## Architecture

-   **Backend (api)**: Spring Boot application with REST controllers, services, and in-memory data storage
-   **CLI (cli)**: Standalone Java application that makes HTTP requests to the backend API
-   **Separation**: Backend and CLI are completely independent modules with no shared code dependencies
-   **Validation**: The CLI ensures required parameters are provided, while the API handles value validation and returns clear error messages
-   **Error Handling**: The CLI displays clean error messages extracted from API responses, making it easy to understand what went wrong
