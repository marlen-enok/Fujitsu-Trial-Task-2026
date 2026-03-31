package com.fujitsu.trial.config;

import com.fujitsu.trial.model.City;
import com.fujitsu.trial.model.VehicleType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToCityConverter());
        registry.addConverter(new StringToVehicleTypeConverter());
    }

    // Concrete class for City conversion
    private static class StringToCityConverter implements Converter<String, City> {
        @Override
        public City convert(String source) {
            return City.valueOf(source.toUpperCase());
        }
    }

    // Concrete class for VehicleType conversion
    private static class StringToVehicleTypeConverter implements Converter<String, VehicleType> {
        @Override
        public VehicleType convert(String source) {
            return VehicleType.valueOf(source.toUpperCase());
        }
    }
}