package com.fujitsu.trial.service;

import com.fujitsu.trial.dto.xml.Observations;
import com.fujitsu.trial.dto.xml.Station;
import com.fujitsu.trial.entity.WeatherData;
import com.fujitsu.trial.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Service class responsible for fetching weather data from an external XML API
 * and persisting it to the local database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherDataService {


    @Value("${weather.api.url}")
    private String WEATHER_URL;

    @Value("#{'${weather.api.target-stations}'.split(',')}")
    private List<String> TARGET_STATIONS;

    private final WeatherDataRepository repository;
    private final RestTemplate restTemplate;

    /**
     * Fetches the latest weather observation data from the external API, filters for
     * target stations, and saves the data to the database.
     */
    public void fetchAndSaveWeatherData() {
        try {
            log.info("Fetching weather data from {}", WEATHER_URL);
            Observations observations = restTemplate.getForObject(WEATHER_URL, Observations.class);

            if (observations != null && observations.getStations() != null) {
                // Convert the XML timestamp to a usable LocalDateTime
                LocalDateTime timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(observations.getTimestamp()),
                        ZoneId.of("Europe/Tallinn"));

                // Filter for our target stations and save
                observations.getStations().stream()
                        .filter(station -> TARGET_STATIONS.contains(station.getName()))
                        .forEach(station -> saveWeatherData(station, timestamp));
            }
        } catch (Exception e) {
            log.error("Failed to fetch or save weather data: {}", e.getMessage());
        }
    }

    /**
     * Maps the DTO station data to the JPA entity and saves it.
     *
     * @param station   the XML station data DTO.
     * @param timestamp the parsed observation timestamp.
     */
    private void saveWeatherData(Station station, LocalDateTime timestamp) {
        WeatherData data = WeatherData.builder()
                .stationName(station.getName())
                .wmoCode(station.getWmoCode())
                .airTemperature(station.getAirTemperature())
                .windSpeed(station.getWindSpeed())
                .weatherPhenomenon(station.getWeatherPhenomenon())
                .timestamp(timestamp)
                .build();

        repository.save(data); // Will permanently store as a new entry due to generated ID
        log.info("Saved weather data for station: {}", station.getName());
    }
}