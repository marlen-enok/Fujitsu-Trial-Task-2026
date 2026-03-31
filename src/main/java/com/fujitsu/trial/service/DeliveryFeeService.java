package com.fujitsu.trial.service;

import com.fujitsu.trial.entity.RegionalBaseFee;
import com.fujitsu.trial.entity.WeatherData;
import com.fujitsu.trial.exception.VehicleForbiddenException;
import com.fujitsu.trial.model.City;
import com.fujitsu.trial.model.VehicleType;
import com.fujitsu.trial.repository.RegionalBaseFeeRepository;
import com.fujitsu.trial.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Service class responsible for executing the core business logic of calculating delivery fees.
 * Applies regional base fees and weather-based extra fees.
 */
@Service
@RequiredArgsConstructor
public class DeliveryFeeService {

    private final WeatherDataRepository weatherRepository;
    private final RegionalBaseFeeRepository rbfRepository;

    // Business Rule Constants
    private static final double ATEF_EXTREME_COLD_TEMP = -10.0;
    private static final double ATEF_EXTREME_COLD_FEE = 1.0;
    private static final double ATEF_COLD_TEMP_UPPER = 0.0;
    private static final double ATEF_COLD_FEE = 0.5;

    private static final double WSEF_HIGH_WIND_LOWER = 10.0;
    private static final double WSEF_HIGH_WIND_UPPER = 20.0;
    private static final double WSEF_HIGH_WIND_FEE = 0.5;

    private static final double WPEF_SNOW_FEE = 1.0;
    private static final double WPEF_RAIN_FEE = 0.5;

    /**
     * Calculates the total delivery fee for a specific city, vehicle, and time.
     *
     * @param city        the enum representation of the city.
     * @param vehicleType the enum representation of the vehicle type.
     * @param requestTime the optional requested time for historical calculations.
     * @return the total calculated delivery fee in Euros.
     * @throws VehicleForbiddenException if the weather conditions forbid the selected vehicle.
     * @throws IllegalArgumentException if required weather or base fee data is missing.
     */
    public Double calculateFee(City city, VehicleType vehicleType, LocalDateTime requestTime) {
        LocalDateTime targetTime = (requestTime != null) ? requestTime : LocalDateTime.now();
        String stationName = city.getStationName();

        // Fetch weather valid at that specific time
        WeatherData weather = weatherRepository.findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(stationName, targetTime)
                .orElseThrow(() -> new IllegalArgumentException("Weather data not found for station: " + stationName + " at time: " + targetTime));

        // Fetch dynamic Base Fee valid at that specific time
        double rbf = calculateDynamicRBF(city.name(), vehicleType.name(), targetTime);

        double atef = calculateATEF(vehicleType, weather.getAirTemperature());
        double wsef = calculateWSEF(vehicleType, weather.getWindSpeed());
        double wpef = calculateWPEF(vehicleType, weather.getWeatherPhenomenon());

        return rbf + atef + wsef + wpef;
    }

    /**
     * Retrieves the active Regional Base Fee (RBF) from the database for the given time.
     *
     * @param cityStr        the name of the city.
     * @param vehicleTypeStr the type of the vehicle.
     * @param targetTime     the timestamp to validate the rule against.
     * @return the base fee amount.
     */
    private double calculateDynamicRBF(String cityStr, String vehicleTypeStr, LocalDateTime targetTime) {
        RegionalBaseFee rule = rbfRepository.findFirstByCityIgnoreCaseAndVehicleTypeIgnoreCaseAndValidFromLessThanEqualOrderByValidFromDesc(
                        cityStr, vehicleTypeStr, targetTime)
                .orElseThrow(() -> new IllegalArgumentException("No valid Regional Base Fee rule found for " + cityStr + " / " + vehicleTypeStr));
        return rule.getFee();
    }

    /**
     * Calculates the Air Temperature Extra Fee (ATEF).
     *
     * @param vehicleType the type of the vehicle.
     * @param temp        the current air temperature.
     * @return the extra fee applied due to temperature.
     */
    private double calculateATEF(VehicleType vehicleType, Double temp) {
        if (vehicleType == VehicleType.CAR) return 0.0;

        if (temp < ATEF_EXTREME_COLD_TEMP) return ATEF_EXTREME_COLD_FEE;
        if (temp >= ATEF_EXTREME_COLD_TEMP && temp <= ATEF_COLD_TEMP_UPPER) return ATEF_COLD_FEE;
        return 0.0;
    }

    /**
     * Calculates the Wind Speed Extra Fee (WSEF).
     *
     * @param vehicleType the type of the vehicle.
     * @param windSpeed   the current wind speed in m/s.
     * @return the extra fee applied due to wind speed.
     * @throws VehicleForbiddenException if wind speed exceeds maximum safe limits for bikes.
     */
    private double calculateWSEF(VehicleType vehicleType, Double windSpeed) {
        if (vehicleType != VehicleType.BIKE) return 0.0;

        if (windSpeed >= WSEF_HIGH_WIND_LOWER && windSpeed <= WSEF_HIGH_WIND_UPPER) return WSEF_HIGH_WIND_FEE;
        if (windSpeed > WSEF_HIGH_WIND_UPPER) {
            throw new VehicleForbiddenException("Usage of selected vehicle type is forbidden"); // [cite: 83]
        }
        return 0.0;
    }

    /**
     * Calculates the Weather Phenomenon Extra Fee (WPEF).
     *
     * @param vehicleType the type of the vehicle.
     * @param phenomenon  the current weather phenomenon string.
     * @return the extra fee applied due to weather phenomenon.
     * @throws VehicleForbiddenException if the weather phenomenon is too dangerous for the vehicle.
     */
    private double calculateWPEF(VehicleType vehicleType, String phenomenon) {
        if (vehicleType == VehicleType.CAR) return 0.0; // [cite: 84]
        if (phenomenon == null || phenomenon.isBlank()) return 0.0;

        String lowerPhenomenon = phenomenon.toLowerCase();
        if (lowerPhenomenon.contains("snow") || lowerPhenomenon.contains("sleet")) return WPEF_SNOW_FEE; // [cite: 85]
        if (lowerPhenomenon.contains("rain") || lowerPhenomenon.contains("shower")) return WPEF_RAIN_FEE; // [cite: 86]
        if (lowerPhenomenon.contains("glaze") || lowerPhenomenon.contains("hail") || lowerPhenomenon.contains("thunder")) {
            throw new VehicleForbiddenException("Usage of selected vehicle type is forbidden"); // [cite: 87]
        }
        return 0.0;
    }
}