package com.airpulse.backend.dto.res;

import com.airpulse.backend.enums.AnomalyType;
import java.time.LocalDateTime;

public record AnomalyRecordResponse(
        Long id,
        Long measurementId,
        String parameter,
        Double latitude,
        Double longitude,
        AnomalyType anomalyType,
        Double value,
        Double referenceValue,
        Double deviationPercentage,
        LocalDateTime detectedAt
) {}