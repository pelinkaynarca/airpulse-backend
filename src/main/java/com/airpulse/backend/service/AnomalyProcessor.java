package com.airpulse.backend.service;

import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;

public interface AnomalyProcessor {

    boolean processMeasurementForAnomalies(AirQualityMeasurementResponse measurement);
}