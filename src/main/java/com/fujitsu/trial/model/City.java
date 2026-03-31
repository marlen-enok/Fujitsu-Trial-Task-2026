package com.fujitsu.trial.model;

/**
 * Enumeration of supported cities for the food delivery application.
 * Maps the application's city representations to their corresponding meteorological station names.
 */

public enum City {
    TALLINN("Tallinn-Harku"),
    TARTU("Tartu-Tõravere"),
    PÄRNU("Pärnu");

    private final String stationName;

    /**
     * Constructs a City enum with its matching station name.
     * @param stationName the official station name from the weather API.
     */
    City(String stationName) {
        this.stationName = stationName;
    }

    /**
     * Gets the corresponding weather station name for the city.
     * @return the station name string.
     */
    public String getStationName() {
        return stationName;
    }
}