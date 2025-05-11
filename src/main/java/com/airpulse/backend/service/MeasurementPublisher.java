package com.airpulse.backend.service;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;

public interface MeasurementPublisher {

    void publishMeasurement(AirQualityMeasurementRequest measurement);
}