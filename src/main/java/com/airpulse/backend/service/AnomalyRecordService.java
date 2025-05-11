package com.airpulse.backend.service;

import com.airpulse.backend.dto.res.AnomalyRecordResponse;
import com.airpulse.backend.enums.AnomalyType;

import java.time.Instant;
import java.util.List;

public interface AnomalyRecordService {

    AnomalyRecordResponse getById(Long id);

    List<AnomalyRecordResponse> getByTimeframe(Instant start, Instant end);

    // start datetime and end datetime can be null
    List<AnomalyRecordResponse> getInRegion(
            Double minLat, Double maxLat, Double minLon, Double maxLon,
            Instant start, Instant end);

    // start datetime and end datetime can be null
    List<AnomalyRecordResponse> getByParameter(String parameter, Instant start, Instant end);

    List<AnomalyRecordResponse> getByType(AnomalyType anomalyType);

    List<AnomalyRecordResponse> getLatest(int limit);
}