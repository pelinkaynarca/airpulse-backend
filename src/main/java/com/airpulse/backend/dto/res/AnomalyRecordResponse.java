package com.airpulse.backend.dto.res;

import com.airpulse.backend.enums.AnomalyType;
import java.time.LocalDateTime;

public record AnomalyRecordResponse(
        Long id,
        Long measurementId,
        AnomalyType anomalyType,
        Double deviationPercentage,
        LocalDateTime detectedAt
) {}