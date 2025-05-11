package com.airpulse.backend.dto.res;

import com.airpulse.backend.enums.AnomalyType;

import java.time.Instant;

public record AnomalyRecordResponse(
        Long id,
        Long measurementId,
        AnomalyType anomalyType,
        Double deviationPercentage,
        Instant detectedAt
) {}