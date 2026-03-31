package com.fujitsu.trial.controller;

import com.fujitsu.trial.dto.DeliveryFeeRequest;
import com.fujitsu.trial.dto.DeliveryFeeResponse;
import com.fujitsu.trial.exception.VehicleForbiddenException;
import com.fujitsu.trial.model.City;
import com.fujitsu.trial.model.VehicleType;
import com.fujitsu.trial.service.DeliveryFeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeliveryFeeControllerTest {

    private DeliveryFeeService service;
    private DeliveryFeeController controller;

    @BeforeEach
    void setUp() {
        service = mock(DeliveryFeeService.class);
        controller = new DeliveryFeeController(service);
    }

    @Test
    void getDeliveryFee_ValidRequest_ReturnsCalculatedFee() {
        // Arrange
        when(service.calculateFee(eq(City.TARTU), eq(VehicleType.BIKE), any())).thenReturn(4.0);

        DeliveryFeeRequest request = new DeliveryFeeRequest();
        request.setCity(City.TARTU);
        request.setVehicleType(VehicleType.BIKE);

        // Act
        ResponseEntity<DeliveryFeeResponse> response = controller.getDeliveryFee(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4.0, response.getBody().getTotalDeliveryFee());
        assertEquals("TARTU", response.getBody().getCity());
        assertEquals("BIKE", response.getBody().getVehicleType());
    }

    @Test
    void getDeliveryFee_WithHistoricalTime_ReturnsCalculatedFee() {
        // Arrange
        LocalDateTime requestTime = LocalDateTime.of(2026, 3, 30, 12, 0);
        when(service.calculateFee(eq(City.TARTU), eq(VehicleType.CAR), eq(requestTime))).thenReturn(3.5);

        DeliveryFeeRequest request = new DeliveryFeeRequest();
        request.setCity(City.TARTU);
        request.setVehicleType(VehicleType.CAR);
        request.setTime(requestTime);

        // Act
        ResponseEntity<DeliveryFeeResponse> response = controller.getDeliveryFee(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3.5, response.getBody().getTotalDeliveryFee());
    }

    @Test
    void getDeliveryFee_ForbiddenVehicle_ThrowsException() {
        // Arrange
        when(service.calculateFee(eq(City.TALLINN), eq(VehicleType.BIKE), any()))
                .thenThrow(new VehicleForbiddenException("Usage of selected vehicle type is forbidden"));

        DeliveryFeeRequest request = new DeliveryFeeRequest();
        request.setCity(City.TALLINN);
        request.setVehicleType(VehicleType.BIKE);

        // Act & Assert
        VehicleForbiddenException exception = assertThrows(VehicleForbiddenException.class, () -> {
            controller.getDeliveryFee(request);
        });

        assertEquals("Usage of selected vehicle type is forbidden", exception.getMessage());
    }
}