package com.airpulse.backend.controller;

import com.airpulse.backend.dto.res.AnomalyRecordResponse;
import com.airpulse.backend.enums.AirQualityParameter;
import com.airpulse.backend.enums.AnomalyType;
import com.airpulse.backend.service.AnomalyRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
@Slf4j
public class AnomalyRecordController {

    private final AnomalyRecordService anomalyRecordService;

    @GetMapping("/{id}")
    public ResponseEntity<AnomalyRecordResponse> getAnomalyById(
            @PathVariable(name = "id") Long id) {
        AnomalyRecordResponse response = anomalyRecordService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/timeframe")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesByTimeframe(
            @RequestParam(name = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(name = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getByTimeframe(start, end);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/region")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesInRegion(
            @RequestParam(name = "minLat") Double minLat,
            @RequestParam(name = "maxLat") Double maxLat,
            @RequestParam(name = "minLon") Double minLon,
            @RequestParam(name = "maxLon") Double maxLon,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getInRegion(
                minLat, maxLat, minLon, maxLon, start, end);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/parameter/{parameter}")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesByParameter(
            @PathVariable(name = "parameter") AirQualityParameter parameter,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getByParameter(parameter, start, end);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesByType(
            @PathVariable(name = "type") AnomalyType type) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getByType(type);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<AnomalyRecordResponse>> getLatest(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getLatest(limit);
        return ResponseEntity.ok(anomalies);
    }
}