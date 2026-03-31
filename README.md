# Delivery Fee Calculator - Fujitsu Trial Task 2026

This is a Spring Boot application designed as a sub-functionality of a food delivery application. It calculates the delivery fee for couriers based on regional base fees, vehicle types, and real-time or historical weather conditions.

## Technologies Used
* **Java 21**
* **Spring Boot** (Spring Web, Spring Data JPA, Spring Scheduling)
* **H2 Database** (In-memory, persistent storage of rules and weather data)
* **Jackson XML** (For parsing EEA weather data)
* **JUnit 5 & Mockito** (For unit testing)
* **Swagger/OpenAPI** (For API documentation)

## Features
### Core Requirements
* **Scheduled Weather Data Import:** A configurable cron job automatically fetches real-time weather data from the Estonian Environment Agency portal. By default, this runs at 15 minutes past every hour.
* **Persistent Storage:** Historical weather data is permanently stored in the H2 database; new entries are inserted rather than overwriting existing ones.
* **Dynamic Fee Calculation:** Calculates the total delivery fee by aggregating the Regional Base Fee (RBF) and Extra Fees (ATEF, WSEF, WPEF) based on specific business rules.
* **Safety Restrictions:** Automatically rejects delivery requests and throws a specific error message ("Usage of selected vehicle type is forbidden") if weather conditions are dangerous (e.g., high winds for bikes, or glaze/hail/thunder for scooters/bikes).

### Bonus Implementations
* **Historical Calculations:** The calculation endpoint supports an optional datetime parameter. If provided, the calculation uses the weather conditions and business rules that were valid at that exact historical time.
* **Rule Management (CRUD):** A dedicated set of REST endpoints allows administrators to Create, Read, Update, and Delete the regional base fee rules dynamically.

## Getting Started

### Prerequisites
* Java 21 or higher
* Gradle

### Running the Application
1. Clone the repository and navigate to the root directory.
2. Run the application using your build tool:
   ```bash
   ./gradlew bootRun
   ```
3. The application will start on `http://localhost:8080`.
4. On startup, the application automatically seeds the H2 database with the default Regional Base Fees and triggers an immediate fetch of the latest weather data.

## API Documentation

Once the application is running, you can access the Swagger UI to interact with the endpoints directly:
* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI Specs:** `http://localhost:8080/v3/api-docs`

## API Endpoints

### 1. Calculate Delivery Fee
* **Endpoint:** `GET /api/delivery-fee`
* **Description:** Enables other parts of the application to request delivery fees according to input parameters.
* **Query Parameters:**
    * `city` (Required): `TALLINN`, `TARTU`, or `PÄRNU`.
    * `vehicleType` (Required): `CAR`, `SCOOTER`, or `BIKE`.
    * `time` (Optional): ISO Date-Time format (e.g., `2026-03-31T12:00:00`). Defaults to current time if omitted.
* **Example Request:**
  ```
  GET /api/delivery-fee?city=TARTU&vehicleType=BIKE
  ```
* **Example Response (200 OK):** In response to the request, the total delivery fee must be given.
  ```json
  {
    "totalDeliveryFee": 4.0,
    "city": "TARTU",
    "vehicleType": "BIKE"
  }
  ```
* **Example Error Response (403 Forbidden):** Returns an error message if the vehicle type is forbidden due to weather conditions.
  ```text
  Usage of selected vehicle type is forbidden
  ```

### 2. Rule Management (Bonus)
These endpoints allow for managing the business rules for base fees:
* `GET /api/rules/base-fee` - Retrieve all regional base fee rules.
* `POST /api/rules/base-fee` - Create a new rule.
* `PUT /api/rules/base-fee/{id}` - Update an existing rule.
* `DELETE /api/rules/base-fee/{id}` - Delete a rule.

## Architecture & Design Decisions
* **Type Safety with Enums:** Inputs for City and Vehicle Type are strictly typed using Java Enums to prevent invalid strings and ensure clean data routing.
* **Global Exception Handling:** Standardized error handling using a `@ControllerAdvice` global handler. Custom exceptions like `VehicleForbiddenException` trigger a clean HTTP response instead of a standard server error stack trace, fulfilling error handling requirements.
* **Clean Code:** Magic numbers for business rules are extracted into clear constants within the Service layer for better human readability and maintainability.
* **Historical Data Accuracy:** Both the weather data and the base fee rules track their activation timestamps (`timestamp` and `validFrom`), ensuring that historical queries perfectly reflect the state of the system at the requested time without overwriting existing data.
* **Layered Architecture:** The project utilizes a standard Spring Boot tiered architecture (Controller, Service, Repository, Entity/Model) to separate concerns and ensure OOP best practices.

## Testing
The application includes test coverage for both the Controller and Service layers using JUnit 5 and Mockito.

Run the unit tests using Gradle:
   ```bash
   ./gradlew test
   ```