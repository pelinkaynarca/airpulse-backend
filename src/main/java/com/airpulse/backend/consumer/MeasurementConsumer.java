package com.airpulse.backend.consumer;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.service.AirQualityMeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeasurementConsumer {

    private final AirQualityMeasurementService airQualityMeasurementService;

    @RabbitListener(queues = "${rabbitmq.queue.name:air-quality-measurements}")
    public void consumeMeasurement(AirQualityMeasurementRequest request) {
        log.info("Received measurement from queue: parameter={}, value={}, lat={}, lon={}",
                request.parameter(), request.value(),
                request.latitude(), request.longitude());

        try {
            airQualityMeasurementService.addAirQualityMeasurement(request);
            log.info("Successfully processed measurement from queue");
        } catch (Exception e) {
            log.error("Error processing measurement from queue: {}", e.getMessage(), e);
            throw e;
        }
    }
}