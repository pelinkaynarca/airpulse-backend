package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.service.MeasurementPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeasurementPublisherImpl implements MeasurementPublisher {

    private final AmqpTemplate amqpTemplate;

    @Value("${rabbitmq.exchange.name:air-quality-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:air-quality.measurement}")
    private String routingKey;

    @Override
    public void publishMeasurement(AirQualityMeasurementRequest measurement) {
        try {
            log.info("Publishing measurement to queue: parameter={}, value={}, lat={}, lon={}",
                    measurement.parameter(), measurement.value(),
                    measurement.latitude(), measurement.longitude());

            amqpTemplate.convertAndSend(exchangeName, routingKey, measurement);

            log.info("Successfully published measurement to queue");
        } catch (Exception e) {
            log.error("Failed to publish measurement to queue: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish measurement to queue", e);
        }
    }
}