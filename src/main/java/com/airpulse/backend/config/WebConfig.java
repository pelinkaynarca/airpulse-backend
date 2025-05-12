package com.airpulse.backend.config;

import com.airpulse.backend.enums.AirQualityParameter;
import com.airpulse.backend.enums.AnomalyType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToAirQualityParameterConverter());
        registry.addConverter(new StringToAnomalyTypeConverter());
    }

    private static class StringToAirQualityParameterConverter implements Converter<String, AirQualityParameter> {
        @Override
        public AirQualityParameter convert(String source) {
            return AirQualityParameter.fromString(source);
        }
    }

    private static class StringToAnomalyTypeConverter implements Converter<String, AnomalyType> {
        @Override
        public AnomalyType convert(String source) {
            try {
                return AnomalyType.valueOf(source);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid anomaly type: " + source);
            }
        }
    }
}