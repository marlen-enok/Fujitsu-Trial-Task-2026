package com.fujitsu.trial.config;

import com.fujitsu.trial.model.City;
import com.fujitsu.trial.model.VehicleType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration class to customize Spring MVC behavior.
 * Maps string request parameters to Enums case-insensitively.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registers custom formatters/converters for the application.
     *
     * @param registry the {@link FormatterRegistry} to configure.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter((Converter<String, City>)
                source -> City.valueOf(source.toUpperCase()));

        registry.addConverter((Converter<String, VehicleType>)
                source -> VehicleType.valueOf(source.toUpperCase()));
    }
}