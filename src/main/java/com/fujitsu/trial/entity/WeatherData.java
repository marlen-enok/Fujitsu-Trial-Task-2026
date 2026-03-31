package com.fujitsu.trial.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a historical weather observation imported from the external API.
 */
@Entity
@Table(name = "weather_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Unique database identifier.
    private Long id;

    // The name of the weather station.
    private String stationName;

    // The World Meteorological Organization code for the station.
    private String wmoCode;

    // Air temperature in Celsius.
    private Double airTemperature;

    // Wind speed recorded in meters per second.
    private Double windSpeed;

    // Textual description of current weather conditions ("Light snow shower" etc).
    private String weatherPhenomenon;

    // The specific date and time the observation was recorded.
    private LocalDateTime timestamp;
}