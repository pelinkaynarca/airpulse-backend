package com.airpulse.backend.service;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;

import java.util.List;

public interface AirQualityMeasurementService {
    List<AirQualityMeasurementResponse> getAllMeasurements();
    void addAirQualityMeasurement(AirQualityMeasurementRequest request);
    AirQualityMeasurementResponse getNearestAndLatestMeasurement(double latitude, double longitude);
}
