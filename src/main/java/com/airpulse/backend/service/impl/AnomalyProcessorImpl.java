package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.enums.AnomalyType;
import com.airpulse.backend.event.AnomalyDetectedEvent;
import com.airpulse.backend.service.AnomalyDetector;
import com.airpulse.backend.service.AnomalyProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnomalyProcessorImpl implements AnomalyProcessor {

    private final List<AnomalyDetector> detectors;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean processMeasurementForAnomalies(AirQualityMeasurementResponse measurementResponse) {
        log.debug("Processing measurement {} for anomalies", measurementResponse.id());

        Map<AnomalyType, Double> detectedAnomalies = new HashMap<>();

        // run all detectors
        for (AnomalyDetector detector : detectors) {
            String detectorName = detector.getClass().getSimpleName();
            log.debug("Running detector: {}", detectorName);

            if (detector.detectAnomaly(measurementResponse)) {
                AnomalyType anomalyType = detector.getAnomalyType();
                Double deviationPercentage = detector.getDeviationPercentage();

                detectedAnomalies.put(anomalyType, deviationPercentage);

                log.info("ANOMALY DETECTED: Type={}, Parameter={}, Value={}, Dev={}%",
                        anomalyType,
                        measurementResponse.parameter(),
                        measurementResponse.value(),
                        String.format("%.2f", deviationPercentage != null ? deviationPercentage : 0.0));
            } else {
                log.debug("No anomaly detected by {}", detectorName);
            }
        }

        if (!detectedAnomalies.isEmpty()) {
            log.info("Publishing anomaly event for measurement {} with {} anomaly types",
                    measurementResponse.id(), detectedAnomalies.size());

            AnomalyDetectedEvent event = AnomalyDetectedEvent.builder()
                    .source(this)
                    .measurementId(measurementResponse.id())
                    .detectedAnomalies(detectedAnomalies)
                    .build();

            eventPublisher.publishEvent(event);
            return true;
        }

        log.debug("No anomalies detected for measurement {}", measurementResponse.id());
        return false;
    }
}