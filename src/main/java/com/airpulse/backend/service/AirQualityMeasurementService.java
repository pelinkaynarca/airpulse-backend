package com.airpulse.backend.service;

import com.airpulse.backend.dto.AirQualityMeasurementRequest;

public interface AirQualityMeasurementService {
    void addAirQualityMeasurement(AirQualityMeasurementRequest request);
}
