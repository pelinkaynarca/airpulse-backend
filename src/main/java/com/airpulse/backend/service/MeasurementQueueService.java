package com.airpulse.backend.service;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;

public interface MeasurementQueueService {
    void sendToQueue(AirQualityMeasurementRequest request);
}