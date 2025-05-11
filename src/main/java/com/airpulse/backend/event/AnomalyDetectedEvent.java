package com.airpulse.backend.event;

import com.airpulse.backend.enums.AnomalyType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class AnomalyDetectedEvent extends ApplicationEvent {

    private final Long measurementId;
    private final Map<AnomalyType, Double> detectedAnomalies; //

    @Builder
    public AnomalyDetectedEvent(Object source,
                                Long measurementId,
                                Map<AnomalyType, Double> detectedAnomalies) {
        super(source);
        this.measurementId = measurementId;
        this.detectedAnomalies = detectedAnomalies;
    }
}