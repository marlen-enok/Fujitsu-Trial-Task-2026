package com.fujitsu.trial.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.BindException;

/**
 * Global exception handler to intercept application exceptions and return standardized HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions thrown when weather conditions are too dangerous for a vehicle.
     *
     * @param ex the thrown {@link VehicleForbiddenException}.
     * @return a 403 Forbidden response with the required error message.
     */
    @ExceptionHandler(VehicleForbiddenException.class)
    public ResponseEntity<String> handleVehicleForbidden(VehicleForbiddenException ex) {
        // Forbidden conditions return a specific message
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles general illegal arguments, such as missing data for calculations.
     *
     * @param ex the thrown {@link IllegalArgumentException}.
     * @return a 400 Bad Request response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        // Catches enum mapping errors or missing data gracefully
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors when API request parameters are invalid, missing, or mistyped.
     *
     * @param ex the validation exception.
     * @return a 400 Bad Request response summarizing the validation failure.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<String> handleValidationExceptions(Exception ex) {
        return new ResponseEntity<>("Invalid or missing input parameters (City or VehicleType).", HttpStatus.BAD_REQUEST);
    }
}