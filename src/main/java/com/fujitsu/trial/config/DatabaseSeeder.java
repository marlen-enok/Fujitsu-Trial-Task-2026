package com.fujitsu.trial.config;

import com.fujitsu.trial.entity.RegionalBaseFee;
import com.fujitsu.trial.repository.RegionalBaseFeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Configuration component that seeds the database with initial regional base fees
 * upon application startup, ensuring the application is immediately usable.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RegionalBaseFeeRepository rbfRepository;

    /**
     * Executes on application startup to insert default fee rules if the database is empty.
     *
     * @param args incoming main method arguments.
     */
    @Override
    public void run(String... args) {
        if (rbfRepository.count() == 0) {
            log.info("Database is empty. Populating default Regional Base Fees...");
            LocalDateTime defaultStartTime = LocalDateTime.now().minusDays(1);

            List<RegionalBaseFee> initialFees = List.of(
                    new RegionalBaseFee(null, "Tallinn", "Car", 4.0, defaultStartTime),
                    new RegionalBaseFee(null, "Tallinn", "Scooter", 3.5, defaultStartTime),
                    new RegionalBaseFee(null, "Tallinn", "Bike", 3.0, defaultStartTime),
                    new RegionalBaseFee(null, "Tartu", "Car", 3.5, defaultStartTime),
                    new RegionalBaseFee(null, "Tartu", "Scooter", 3.0, defaultStartTime),
                    new RegionalBaseFee(null, "Tartu", "Bike", 2.5, defaultStartTime),
                    new RegionalBaseFee(null, "Pärnu", "Car", 3.0, defaultStartTime),
                    new RegionalBaseFee(null, "Pärnu", "Scooter", 2.5, defaultStartTime),
                    new RegionalBaseFee(null, "Pärnu", "Bike", 2.0, defaultStartTime)
            );

            rbfRepository.saveAll(initialFees);
            log.info("Default Regional Base Fees successfully loaded.");
        }
    }
}