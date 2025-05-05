package com.airpulse.backend.controller;

import com.airpulse.backend.dto.res.AnomalyRecordResponse;
import com.airpulse.backend.enums.AnomalyType;
import com.airpulse.backend.service.AnomalyRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
@RequiredArgsConstructor
@Slf4j
public class AnomalyRecordController {

    private final AnomalyRecordService anomalyRecordService;

    @GetMapping("/{id}")
    public ResponseEntity<AnomalyRecordResponse> getAnomalyById(
            @PathVariable Long id) {
        AnomalyRecordResponse response = anomalyRecordService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/timeframe")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesByTimeframe(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getByTimeframe(start, end);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/region")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesInRegion(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getInRegion(
                minLat, maxLat, minLon, maxLon, start, end);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/parameter/{parameter}")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesByParameter(
            @PathVariable String parameter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getByParameter(parameter, start, end);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<AnomalyRecordResponse>> getAnomaliesByType(
            @PathVariable AnomalyType type) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getByType(type);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<AnomalyRecordResponse>> getLatest(
            @RequestParam(defaultValue = "10") int limit) {
        List<AnomalyRecordResponse> anomalies = anomalyRecordService.getLatest(limit);
        return ResponseEntity.ok(anomalies);
    }
}