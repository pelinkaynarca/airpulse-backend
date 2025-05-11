package com.airpulse.backend.controller;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.enums.AirQualityParameter;
import com.airpulse.backend.service.AirQualityMeasurementService;
import com.airpulse.backend.service.MeasurementPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/air-quality-measurements")
@RequiredArgsConstructor
@Slf4j
public class AirQualityMeasurementController {

    private final AirQualityMeasurementService airQualityMeasurementService;
    private final MeasurementPublisher measurementPublisher;

    @PostMapping
    public ResponseEntity<Void> queueMeasurement(
            @Valid @RequestBody AirQualityMeasurementRequest request) {
        log.info("Received measurement request: {} = {}", request.parameter(), request.value());

        measurementPublisher.publishMeasurement(request);

        // return 202 accepted (request queued for processing)
        return ResponseEntity.accepted().build();
    }

//    // for testing
//    @PostMapping("/direct")
//    public ResponseEntity<Void> addMeasurementDirect(
//            @Valid @RequestBody AirQualityMeasurementRequest request) {
//        airQualityMeasurementService.addAirQualityMeasurement(request);
//        return ResponseEntity.status(201).build();
//    }

    @GetMapping("/all")
    public ResponseEntity<List<AirQualityMeasurementResponse>> getAllMeasurements() {
        return ResponseEntity.ok(airQualityMeasurementService.getAllMeasurements());
    }

    @GetMapping("/parameter")
    public ResponseEntity<List<AirQualityMeasurementResponse>> getMeasurementsByParameter(
            @RequestParam AirQualityParameter parameter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<AirQualityMeasurementResponse> measurements =
                airQualityMeasurementService.getMeasurementsByParameter(parameter, start, end);

        return ResponseEntity.ok(measurements);
    }

    @GetMapping("/region")
    public ResponseEntity<List<AirQualityMeasurementResponse>> getMeasurementsInRegion(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<AirQualityMeasurementResponse> measurements =
                airQualityMeasurementService.getMeasurementsInRegion(minLat, maxLat, minLon, maxLon, start, end);

        return ResponseEntity.ok(measurements);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<AirQualityMeasurementResponse>> getLatestMeasurements(
            @RequestParam(defaultValue = "10") int limit) {
        List<AirQualityMeasurementResponse> measurements = airQualityMeasurementService.getLatestMeasurements(limit);
        return ResponseEntity.ok(measurements);
    }

    @GetMapping("/nearest")
    public ResponseEntity<AirQualityMeasurementResponse> getNearestAndLatestMeasurement(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        AirQualityMeasurementResponse response =
                airQualityMeasurementService.getNearestAndLatestMeasurement(latitude, longitude);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageForParameterInRegion(
            @RequestParam AirQualityParameter parameter,
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Double average = airQualityMeasurementService.calculateAverageForParameterInRegionAndTimeframe(
                parameter, minLat, maxLat, minLon, maxLon, start, end);

        if (average == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(average);
    }
}