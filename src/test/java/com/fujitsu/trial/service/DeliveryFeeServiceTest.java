package com.fujitsu.trial.service;

import com.fujitsu.trial.entity.RegionalBaseFee;
import com.fujitsu.trial.entity.WeatherData;
import com.fujitsu.trial.exception.VehicleForbiddenException;
import com.fujitsu.trial.model.City;
import com.fujitsu.trial.model.VehicleType;
import com.fujitsu.trial.repository.RegionalBaseFeeRepository;
import com.fujitsu.trial.repository.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeServiceTest {

    @Mock
    private WeatherDataRepository weatherRepository;

    @Mock
    private RegionalBaseFeeRepository rbfRepository;

    @InjectMocks
    private DeliveryFeeService service;

    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.of(2026, 3, 31, 12, 0);
    }

    @Test
    void calculateFee_NormalConditions_ReturnsCorrectFee() {
        // Arrange: Mock Base Fee -> Enums use .name() which returns upper-case
        RegionalBaseFee mockRule = new RegionalBaseFee(1L, "TARTU", "BIKE", 2.5, testTime.minusDays(1));
        when(rbfRepository.findFirstByCityIgnoreCaseAndVehicleTypeIgnoreCaseAndValidFromLessThanEqualOrderByValidFromDesc(
                eq("TARTU"), eq("BIKE"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockRule));

        // Arrange: Mock Weather Data (Temp: -2.1, Wind: 4.7, Phenom: Light snow shower)
        WeatherData mockWeather = new WeatherData(1L, "Tartu-Tõravere", "26242", -2.1, 4.7, "Light snow shower", testTime);
        when(weatherRepository.findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
                eq("Tartu-Tõravere"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockWeather));

        // Act
        Double fee = service.calculateFee(City.TARTU, VehicleType.BIKE, testTime);

        // Assert: Base(2.5) + Temp Extra(0.5) + Wind Extra(0.0) + Phenom Extra(1.0) = 4.0
        assertEquals(4.0, fee);
    }

    @Test
    void calculateFee_HighWind_ThrowsVehicleForbiddenException() {
        // Arrange
        RegionalBaseFee mockRule = new RegionalBaseFee(1L, "TALLINN", "BIKE", 3.0, testTime.minusDays(1));
        when(rbfRepository.findFirstByCityIgnoreCaseAndVehicleTypeIgnoreCaseAndValidFromLessThanEqualOrderByValidFromDesc(
                eq("TALLINN"), eq("BIKE"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockRule));

        // Arrange: Mock Stormy Weather (Wind: 21.0 m/s)
        WeatherData stormWeather = new WeatherData(1L, "Tallinn-Harku", "26038", 5.0, 21.0, "Clear", testTime);
        when(weatherRepository.findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
                eq("Tallinn-Harku"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(stormWeather));

        // Act & Assert
        VehicleForbiddenException exception = assertThrows(VehicleForbiddenException.class, () -> {
            service.calculateFee(City.TALLINN, VehicleType.BIKE, testTime);
        });
        assertEquals("Usage of selected vehicle type is forbidden", exception.getMessage());
    }

    @Test
    void calculateFee_DangerousPhenomenon_ThrowsVehicleForbiddenException() {
        // Arrange
        RegionalBaseFee mockRule = new RegionalBaseFee(1L, "PÄRNU", "SCOOTER", 2.5, testTime.minusDays(1));
        when(rbfRepository.findFirstByCityIgnoreCaseAndVehicleTypeIgnoreCaseAndValidFromLessThanEqualOrderByValidFromDesc(
                eq("PÄRNU"), eq("SCOOTER"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockRule));

        // Arrange: Mock Dangerous Weather (Phenomenon: Hail)
        WeatherData hailWeather = new WeatherData(1L, "Pärnu", "41803", 2.0, 5.0, "Hail", testTime);
        when(weatherRepository.findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
                eq("Pärnu"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(hailWeather));

        // Act & Assert
        VehicleForbiddenException exception = assertThrows(VehicleForbiddenException.class, () -> {
            service.calculateFee(City.PÄRNU, VehicleType.SCOOTER, testTime);
        });
        assertEquals("Usage of selected vehicle type is forbidden", exception.getMessage());
    }
}