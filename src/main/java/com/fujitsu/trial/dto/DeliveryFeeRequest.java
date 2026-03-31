package com.fujitsu.trial.dto;

import com.fujitsu.trial.model.City;
import com.fujitsu.trial.model.VehicleType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class DeliveryFeeRequest {
    @NotNull(message = "City must be provided (TALLINN, TARTU, PÄRNU)")
    private City city;

    @NotNull(message = "Vehicle type must be provided (CAR, SCOOTER, BIKE)")
    private VehicleType vehicleType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime time;
}