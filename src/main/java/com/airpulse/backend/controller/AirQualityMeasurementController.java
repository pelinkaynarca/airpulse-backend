package com.airpulse.backend.controller;

import com.airpulse.backend.dto.AirQualityMeasurementRequest;
import com.airpulse.backend.service.AirQualityMeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/air-quality-measurements")
@RequiredArgsConstructor
public class AirQualityMeasurementController {

    private final AirQualityMeasurementService airQualityMeasurementService;

    @PostMapping
    public ResponseEntity<String> addAirQualityMeasurement(@RequestBody AirQualityMeasurementRequest request) {
        airQualityMeasurementService.addAirQualityMeasurement(request);
        return ResponseEntity.ok("Air Quality Measurement added successfully!");
    }
}
