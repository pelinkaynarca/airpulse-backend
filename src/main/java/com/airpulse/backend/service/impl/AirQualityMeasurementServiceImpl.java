package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.entity.AirQualityMeasurement;
import com.airpulse.backend.enums.AirQualityParameter;
import com.airpulse.backend.exception.ResourceNotFoundException;
import com.airpulse.backend.mapper.AirQualityMeasurementMapper;
import com.airpulse.backend.repository.AirQualityMeasurementRepository;
import com.airpulse.backend.service.AirQualityMeasurementService;
import com.airpulse.backend.service.AnomalyProcessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AirQualityMeasurementServiceImpl implements AirQualityMeasurementService {

    private final AirQualityMeasurementRepository airQualityMeasurementRepository;
    private final AnomalyProcessor anomalyProcessor;

    private static final double DEFAULT_RADIUS = 0.25;

    @Override
    @Transactional
    public void addAirQualityMeasurement(AirQualityMeasurementRequest request) {
        AirQualityMeasurement measurement = AirQualityMeasurementMapper.INSTANCE.mapRequestToEntity(request);
        AirQualityMeasurement savedMeasurement = airQualityMeasurementRepository.save(measurement);
        log.info("Added new measurement: ID={}, parameter={}, value={}",
                savedMeasurement.getId(), savedMeasurement.getParameter(), savedMeasurement.getValue());

        AirQualityMeasurementResponse response = AirQualityMeasurementMapper.INSTANCE.mapEntityToResponse(savedMeasurement);

        boolean anomalyDetected = anomalyProcessor.processMeasurementForAnomalies(response);

        if (anomalyDetected) {
            log.info("Anomaly detected for measurement ID: {}", savedMeasurement.getId());
        }
    }

    @Override
    public AirQualityMeasurementResponse getNearestAndLatestMeasurement(double latitude, double longitude) {
        return airQualityMeasurementRepository.findNearestAndLatest(latitude, longitude, DEFAULT_RADIUS)
                .map(AirQualityMeasurementMapper.INSTANCE::mapEntityToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No recent air quality data found near the given location."));
    }

    @Override
    public List<AirQualityMeasurementResponse> getAllMeasurements() {
        List<AirQualityMeasurement> entities = airQualityMeasurementRepository.findAll();
        return entities.stream()
                .map(AirQualityMeasurementMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    public List<AirQualityMeasurementResponse> getMeasurementsByParameter(
            AirQualityParameter parameter,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        List<AirQualityMeasurement> measurements;

        if (startTime != null && endTime != null) {
            measurements = airQualityMeasurementRepository.findByParameterAndCreatedAtBetween(
                    parameter, startTime, endTime);
        } else {
            measurements = airQualityMeasurementRepository.findByParameter(parameter);
        }

        return measurements.stream()
                .map(AirQualityMeasurementMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    public List<AirQualityMeasurementResponse> getMeasurementsInRegion(
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        List<AirQualityMeasurement> measurements;

        if (startTime != null && endTime != null) {
            measurements = airQualityMeasurementRepository.findByLatitudeBetweenAndLongitudeBetweenAndCreatedAtBetween(
                    minLatitude, maxLatitude, minLongitude, maxLongitude, startTime, endTime);
        } else {
            measurements = airQualityMeasurementRepository.findByLatitudeBetweenAndLongitudeBetween(
                    minLatitude, maxLatitude, minLongitude, maxLongitude);
        }

        return measurements.stream()
                .map(AirQualityMeasurementMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    public List<AirQualityMeasurementResponse> getLatestMeasurements(int limit) {
        List<AirQualityMeasurement> measurements = airQualityMeasurementRepository.findLatestMeasurements(limit);

        return measurements.stream()
                .map(AirQualityMeasurementMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    public Double calculateAverageForParameterInRegionAndTimeframe(
            AirQualityParameter parameter,
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        return airQualityMeasurementRepository.calculateAverageForParameterInRegionAndTimeframe(
                parameter, minLatitude, maxLatitude, minLongitude, maxLongitude, startTime, endTime);
    }
}