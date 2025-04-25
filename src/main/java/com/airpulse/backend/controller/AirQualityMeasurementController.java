package com.airpulse.backend.controller;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.service.AirQualityMeasurementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/air-quality-measurements")
@RequiredArgsConstructor
public class AirQualityMeasurementController {

    private final AirQualityMeasurementService airQualityMeasurementService;

    @GetMapping("/all")
    public ResponseEntity<List<AirQualityMeasurementResponse>> getAllMeasurements() {
        return ResponseEntity.ok(airQualityMeasurementService.getAllMeasurements());
    }

    @GetMapping
    public ResponseEntity<AirQualityMeasurementResponse> getNearestAndLatestMeasurement(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
            AirQualityMeasurementResponse response = airQualityMeasurementService.getNearestAndLatestMeasurement(latitude, longitude);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody @Valid AirQualityMeasurementRequest request) {
        airQualityMeasurementService.addAirQualityMeasurement(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
