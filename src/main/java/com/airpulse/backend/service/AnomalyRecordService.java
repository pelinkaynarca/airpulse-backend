package com.airpulse.backend.service;

import com.airpulse.backend.dto.res.AnomalyRecordResponse;
import com.airpulse.backend.enums.AnomalyType;

import java.time.LocalDateTime;
import java.util.List;

public interface AnomalyRecordService {

    AnomalyRecordResponse createAnomaly(
            Long measurementId,
            AnomalyType anomalyType,
            Double value,
            Double referenceValue,
            Double deviationPercentage);

    AnomalyRecordResponse getById(Long id);

    List<AnomalyRecordResponse> getByTimeframe(LocalDateTime start, LocalDateTime end);

    List<AnomalyRecordResponse> getInRegion(
            Double minLat, Double maxLat, Double minLon, Double maxLon,
            LocalDateTime start, LocalDateTime end);

    List<AnomalyRecordResponse> getByParameter(String parameter, LocalDateTime start, LocalDateTime end);

    List<AnomalyRecordResponse> getByType(AnomalyType anomalyType);

    List<AnomalyRecordResponse> getLatest(int limit);
}