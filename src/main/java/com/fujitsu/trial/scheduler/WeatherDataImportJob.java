package com.fujitsu.trial.scheduler;

import com.fujitsu.trial.repository.RegionalBaseFeeRepository;
import com.fujitsu.trial.service.WeatherDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job component responsible for triggering periodic weather data imports.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherDataImportJob {

    private final WeatherDataService weatherDataService;
    private final RegionalBaseFeeRepository rbfRepository; // Injected to manage DB rules

    /**
     * Executes the weather data import process based on the configured cron expression.
     * Defaults to running 15 minutes past every hour.
     */
    @Scheduled(cron = "${weather.import.cron:0 15 * * * *}")
    public void importWeatherData() {
        log.info("Starting scheduled weather data import...");
        weatherDataService.fetchAndSaveWeatherData();
    }

    // Startup tasks
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Application started. Running initialization tasks...");
        weatherDataService.fetchAndSaveWeatherData();
    }

}