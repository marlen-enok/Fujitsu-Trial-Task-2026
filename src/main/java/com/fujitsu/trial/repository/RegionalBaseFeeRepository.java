package com.fujitsu.trial.repository;

import com.fujitsu.trial.entity.RegionalBaseFee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for managing {@link RegionalBaseFee} entities in the database.
 */
public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, Long> {

    /**
     * Finds the most recent applicable regional base fee rule for a given city and vehicle type
     * that was valid at or before the specified timestamp.
     *
     * @param city        the target city.
     * @param vehicleType the target vehicle type.
     * @param timestamp   the timestamp to evaluate rules against.
     * @return an {@link Optional} containing the applicable fee rule, if found.
     */
    Optional<RegionalBaseFee> findFirstByCityIgnoreCaseAndVehicleTypeIgnoreCaseAndValidFromLessThanEqualOrderByValidFromDesc(
            String city, String vehicleType, LocalDateTime timestamp);
}