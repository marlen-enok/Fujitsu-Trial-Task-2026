package com.fujitsu.trial.controller;

import com.fujitsu.trial.dto.DeliveryFeeRequest;
import com.fujitsu.trial.dto.DeliveryFeeResponse;
import com.fujitsu.trial.service.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling delivery fee calculations.
 * Provides endpoints for external systems to request the total delivery fee.
 */
@RestController
@RequestMapping("/api/delivery-fee")
@RequiredArgsConstructor
public class DeliveryFeeController {

    private final DeliveryFeeService service;

    /**
     * Calculates the total delivery fee based on regional base rules and weather conditions.
     * Supports historical fee calculation if a time parameter is provided.
     *
     * @param request the delivery fee request containing city, vehicle type, and optional time.
     * @return a {@link ResponseEntity} containing the calculated {@link DeliveryFeeResponse}.
     */
    @Operation(summary = "Calculate total delivery fee", description = "Calculates fee based on rules and weather valid at the specified time")
    @GetMapping
    public ResponseEntity<DeliveryFeeResponse> getDeliveryFee(@Valid @ModelAttribute DeliveryFeeRequest request) {
        Double fee = service.calculateFee(request.getCity(), request.getVehicleType(), request.getTime());

        DeliveryFeeResponse response = new DeliveryFeeResponse(
                fee,
                request.getCity().name(),
                request.getVehicleType().name()
        );
        return ResponseEntity.ok(response);
    }
}