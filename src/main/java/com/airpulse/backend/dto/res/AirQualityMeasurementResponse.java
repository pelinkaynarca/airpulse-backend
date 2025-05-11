package com.airpulse.backend.dto.res;

import com.airpulse.backend.enums.AirQualityParameter;

import java.time.Instant;

public record AirQualityMeasurementResponse(
        Long id,
        AirQualityParameter parameter,
        Double value,
        Double latitude,
        Double longitude,
        Instant createdAt
) {}
