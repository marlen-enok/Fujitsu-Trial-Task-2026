package com.fujitsu.trial.repository;

import com.fujitsu.trial.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for managing {@link WeatherData} entities in the database.
 */

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    /**
     * Retrieves the latest weather observation for a specific station that occurred at or
     * before the given requested timestamp.
     *
     * @param stationName the exact name of the observation station.
     * @param timestamp   the timestamp to retrieve data for.
     * @return an {@link Optional} containing the appropriate weather data, if found.
     */
    Optional<WeatherData> findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
            String stationName, LocalDateTime timestamp);
}