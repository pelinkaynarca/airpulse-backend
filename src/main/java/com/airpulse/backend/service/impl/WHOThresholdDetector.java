package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.enums.AirQualityParameter;
import com.airpulse.backend.enums.AnomalyType;
import com.airpulse.backend.service.AnomalyDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WHOThresholdDetector implements AnomalyDetector {

    // WHO guidelines for air quality
    // using shortest averaging time available for each pollutant
    private static final Map<AirQualityParameter, Double> WHO_THRESHOLDS = Map.of(
            AirQualityParameter.PM2_5, 15.0,  // μg/m³ (24-hour AQG)
            AirQualityParameter.PM10, 45.0,   // μg/m³ (24-hour AQG)
            AirQualityParameter.O3, 100.0,    // μg/m³ (8-hour AQG)
            AirQualityParameter.NO2, 200.0,   // μg/m³ (1-hour guideline)
            AirQualityParameter.SO2, 500.0    // μg/m³ (10-minute guideline)
    );

    private Double currentDeviationPercentage;

    @Override
    public boolean detectAnomaly(AirQualityMeasurementResponse measurement) {
        Double threshold = WHO_THRESHOLDS.get(measurement.parameter());

        if (threshold == null) {
            log.debug("No WHO threshold defined for parameter: {}", measurement.parameter());
            return false;
        }

        if (measurement.value() > threshold) {
            // calculate deviation percentage from threshold
            double deviationPercentage = ((measurement.value() - threshold) / threshold) * 100.0;

            log.info("WHO threshold anomaly detected: {} > {} for parameter {} ({}% above threshold)",
                    measurement.value(), threshold, measurement.parameter(), String.format("%.1f", deviationPercentage));

            this.currentDeviationPercentage = deviationPercentage;

            return true;
        }

        return false;
    }

    @Override
    public AnomalyType getAnomalyType() {
        return AnomalyType.WHO_THRESHOLD_EXCEEDED;
    }

    @Override
    public Double getDeviationPercentage() {
        return currentDeviationPercentage;
    }
}