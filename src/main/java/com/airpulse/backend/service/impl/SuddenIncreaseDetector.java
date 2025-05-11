package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.entity.AirQualityMeasurement;
import com.airpulse.backend.enums.AnomalyType;
import com.airpulse.backend.repository.AirQualityMeasurementRepository;
import com.airpulse.backend.service.AnomalyDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuddenIncreaseDetector implements AnomalyDetector {

    private final AirQualityMeasurementRepository airQualityMeasurementRepository;

    // Configuration constants
    private static final int HISTORICAL_HOURS = 24;
    // private static final int MINIMUM_EXCLUSION_MINUTES = 10;
    private static final double PERCENTAGE_THRESHOLD = 50.0; // 50% increase
    private static final double LATITUDE_DELTA = 0.225;   // ~25km
    private static final double LONGITUDE_DELTA = 0.225;  // ~25km
    private static final int MINIMUM_DATA_POINTS = 2;

    // for thread-safe state management
    private static final ThreadLocal<Double> currentDeviationPercentage = new ThreadLocal<>();

    @Override
    public boolean detectAnomaly(AirQualityMeasurementResponse measurementResponse) {
        log.debug("=== SuddenIncreaseDetector - Processing measurement ID: {} ===", measurementResponse.id());
        log.debug("Current measurement: Parameter={}, Value={}, Lat={}, Lon={}",
                measurementResponse.parameter(), measurementResponse.value(),
                measurementResponse.latitude(), measurementResponse.longitude());

        // clear previous state
        currentDeviationPercentage.remove();

        Instant startTime = Instant.now().minus(HISTORICAL_HOURS, ChronoUnit.HOURS);
        Instant endTime = Instant.now();

        log.debug("Searching for historical data from {} to {} within 25km radius", startTime, endTime);

        List<AirQualityMeasurement> recentMeasurements = findHistoricalMeasurements(
                measurementResponse, startTime, endTime);

        log.debug("Found {} historical measurements within 25km radius",
                recentMeasurements.size());

        if (!hasEnoughHistoricalData(recentMeasurements.size())) {
            log.debug("Not enough historical data for sudden increase detection");
            return false;
        }

        double historicalAverage = calculateAverage(recentMeasurements);
        log.debug("Historical average: {}", historicalAverage);

        if (historicalAverage <= 0) {
            log.debug("Historical average is zero or negative, cannot detect sudden increase");
            return false;
        }

        boolean isAnomaly = evaluateSuddenIncrease(measurementResponse.value(), historicalAverage);
        log.debug("=== SuddenIncreaseDetector result: {} ===", isAnomaly);
        return isAnomaly;
    }

    private List<AirQualityMeasurement> findHistoricalMeasurements(
            AirQualityMeasurementResponse measurement,
            Instant startTime,
            Instant endTime) {

        double minLat = measurement.latitude() - LATITUDE_DELTA;
        double maxLat = measurement.latitude() + LATITUDE_DELTA;
        double minLon = measurement.longitude() - LONGITUDE_DELTA;
        double maxLon = measurement.longitude() + LONGITUDE_DELTA;

        log.debug("Search area: Lat [{}, {}], Lon [{}, {}] (25km radius)",
                String.format("%.3f", minLat), String.format("%.3f", maxLat),
                String.format("%.3f", minLon), String.format("%.3f", maxLon));

        return airQualityMeasurementRepository.findByParameterAndLatitudeBetweenAndLongitudeBetweenAndCreatedAtBetween(
                measurement.parameter(),
                minLat, maxLat,
                minLon, maxLon,
                startTime, endTime
        );
    }

    private boolean hasEnoughHistoricalData(int dataPoints) {
        if (dataPoints < MINIMUM_DATA_POINTS) {
            log.debug("Insufficient historical data for sudden increase analysis " +
                    "(need at least {}, found {})", MINIMUM_DATA_POINTS, dataPoints);
            return false;
        }
        return true;
    }

    private double calculateAverage(List<AirQualityMeasurement> measurements) {
        double average = measurements.stream()
                .mapToDouble(AirQualityMeasurement::getValue)
                .average()
                .orElse(0.0);
        log.debug("Calculated average from {} measurements: {}", measurements.size(), average);
        return average;
    }

    private boolean evaluateSuddenIncrease(double currentValue, double historicalAverage) {
        double percentageIncrease = ((currentValue - historicalAverage) / historicalAverage) * 100.0;

        log.debug("Evaluating sudden increase: Current={}, Historical={}, Increase={}%, Threshold={}%",
                currentValue, historicalAverage, String.format("%.1f", percentageIncrease), PERCENTAGE_THRESHOLD);

        if (percentageIncrease <= PERCENTAGE_THRESHOLD) {
            log.debug("No sudden increase detected ({}% <= {}%)",
                    String.format("%.1f", percentageIncrease), PERCENTAGE_THRESHOLD);
            return false;
        }

        // store the deviation percentage in ThreadLocal
        currentDeviationPercentage.set(percentageIncrease);

        log.info("Sudden increase detected: {} is {}% above 24h average of {} within 25km radius",
                currentValue,
                String.format("%.1f", percentageIncrease),
                String.format("%.2f", historicalAverage));

        return true;
    }

    @Override
    public AnomalyType getAnomalyType() {
        return AnomalyType.SUDDEN_INCREASE;
    }

    @Override
    public Double getDeviationPercentage() {
        Double percentage = currentDeviationPercentage.get();
        // clean up ThreadLocal after use
        currentDeviationPercentage.remove();
        return percentage;
    }
}