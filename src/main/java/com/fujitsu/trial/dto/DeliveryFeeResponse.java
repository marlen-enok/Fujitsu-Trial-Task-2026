package com.fujitsu.trial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryFeeResponse {
    private Double totalDeliveryFee;
    private String city;
    private String vehicleType;
}