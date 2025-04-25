package com.airpulse.backend.dto.res;

import java.time.LocalDateTime;

public record AirQualityMeasurementResponse(
        Long id,
        double latitude,
        double longitude,
        double pm25,
        double pm10,
        double no2,
        double so2,
        double o3,
        LocalDateTime timestamp,
        boolean anomalyDetected
) {}
