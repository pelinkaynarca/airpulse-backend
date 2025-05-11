package com.airpulse.backend.service;

import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.enums.AnomalyType;

public interface AnomalyDetector {

    boolean detectAnomaly(AirQualityMeasurementResponse measurement);

    AnomalyType getAnomalyType();

    Double getDeviationPercentage();
}