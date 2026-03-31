package com.fujitsu.trial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing the base delivery fee for a specific city and vehicle type.
 */

@Entity
@Table(name = "regional_base_fee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionalBaseFee {
    // Unique database identifier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The city the rule applies to (Tallinn, Tartu etc).
    private String city;

    // The vehicle type the rule applies to (Car, Bike etc).
    private String vehicleType;

    // The base fee amount
    private Double fee;

    // This allows us to track historical rule changes
    private LocalDateTime validFrom;
}