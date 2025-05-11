package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.res.AnomalyRecordResponse;
import com.airpulse.backend.entity.AirQualityMeasurement;
import com.airpulse.backend.entity.AnomalyRecord;
import com.airpulse.backend.enums.AnomalyType;
import com.airpulse.backend.event.AnomalyDetectedEvent;
import com.airpulse.backend.exception.ResourceNotFoundException;
import com.airpulse.backend.mapper.AnomalyRecordMapper;
import com.airpulse.backend.repository.AirQualityMeasurementRepository;
import com.airpulse.backend.repository.AnomalyRecordRepository;
import com.airpulse.backend.service.AnomalyRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyRecordServiceImpl implements AnomalyRecordService {

    private final AnomalyRecordRepository anomalyRecordRepository;
    private final AirQualityMeasurementRepository measurementRepository;

    @EventListener
    @Transactional
    public void handleAnomalyDetectedEvent(AnomalyDetectedEvent event) {
        log.info("Received anomaly detected event for measurement: {} with {} anomaly types",
                event.getMeasurementId(), event.getDetectedAnomalies().size());

        try {
            // Fetch the actual measurement entity
            AirQualityMeasurement measurement = measurementRepository.findById(event.getMeasurementId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Measurement not found with ID: " + event.getMeasurementId()));

            // Create a separate anomaly record for each detected anomaly type
            for (Map.Entry<AnomalyType, Double> entry : event.getDetectedAnomalies().entrySet()) {
                AnomalyType anomalyType = entry.getKey();
                Double deviationPercentage = entry.getValue();

                AnomalyRecord anomalyRecord = AnomalyRecord.builder()
                        .measurement(measurement)
                        .anomalyType(anomalyType)
                        .deviationPercentage(deviationPercentage)
                        .build();

                AnomalyRecord savedRecord = anomalyRecordRepository.save(anomalyRecord);
                log.info("Saved anomaly record with ID: {} for measurement: {} (Type: {}, Dev: {}%)",
                        savedRecord.getId(), event.getMeasurementId(), anomalyType,
                        String.format("%.2f", deviationPercentage != null ? deviationPercentage : 0.0));
            }
        } catch (Exception e) {
            log.error("Error saving anomaly records for measurement {}: {}",
                    event.getMeasurementId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AnomalyRecordResponse getById(Long id) {
        return anomalyRecordRepository.findById(id)
                .map(AnomalyRecordMapper.INSTANCE::mapEntityToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Anomaly record not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyRecordResponse> getByTimeframe(Instant start, Instant end) {
        validateTimeframe(start, end);

        return anomalyRecordRepository.findByDetectedAtBetween(start, end)
                .stream()
                .map(AnomalyRecordMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyRecordResponse> getInRegion(
            Double minLat, Double maxLat, Double minLon, Double maxLon,
            Instant start, Instant end) {

        validateCoordinates(minLat, maxLat, minLon, maxLon);

        List<AnomalyRecord> anomalies = (start != null && end != null) ?
                anomalyRecordRepository.findInRegionWithTimeframe(minLat, maxLat, minLon, maxLon, start, end) :
                anomalyRecordRepository.findInRegion(minLat, maxLat, minLon, maxLon);

        return anomalies.stream()
                .map(AnomalyRecordMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyRecordResponse> getByParameter(String parameter, Instant start, Instant end) {
        if (parameter == null || parameter.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter cannot be null or empty");
        }

        List<AnomalyRecord> anomalies = (start != null && end != null) ?
                anomalyRecordRepository.findByParameterAndTimeframe(parameter, start, end) :
                anomalyRecordRepository.findByParameter(parameter);

        return anomalies.stream()
                .map(AnomalyRecordMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyRecordResponse> getByType(AnomalyType anomalyType) {
        if (anomalyType == null) {
            throw new IllegalArgumentException("Anomaly type cannot be null");
        }

        return anomalyRecordRepository.findByAnomalyType(anomalyType)
                .stream()
                .map(AnomalyRecordMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnomalyRecordResponse> getLatest(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }

        return anomalyRecordRepository.findTopByOrderByDetectedAtDesc(limit)
                .stream()
                .map(AnomalyRecordMapper.INSTANCE::mapEntityToResponse)
                .toList();
    }

    // Helper methods
    private void validateTimeframe(Instant start, Instant end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    private void validateCoordinates(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        if (minLat == null || maxLat == null || minLon == null || maxLon == null) {
            throw new IllegalArgumentException("Coordinate values cannot be null");
        }

        if (minLat > maxLat) {
            throw new IllegalArgumentException("Minimum latitude cannot be greater than maximum latitude");
        }

        if (minLon > maxLon) {
            throw new IllegalArgumentException("Minimum longitude cannot be greater than maximum longitude");
        }

        if (minLat < -90 || maxLat > 90) {
            throw new IllegalArgumentException("Latitude values must be between -90 and 90");
        }

        if (minLon < -180 || maxLon > 180) {
            throw new IllegalArgumentException("Longitude values must be between -180 and 180");
        }
    }
}