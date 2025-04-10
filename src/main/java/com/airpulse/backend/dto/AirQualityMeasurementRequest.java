package com.airpulse.backend.dto;

import java.time.LocalDateTime;

public record AirQualityMeasurementRequest(
        double latitude,
        double longitude,
        double pm25,
        double pm10,
        double no2,
        double so2,
        double o3,
        LocalDateTime timestamp
) {
}
