package com.airpulse.backend.controller;

import com.airpulse.backend.dto.AirQualityRecordRequest;
import com.airpulse.backend.service.AirQualityRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/air-quality")
@RequiredArgsConstructor
public class AirQualityRecordController {

    private final AirQualityRecordService airQualityRecordService;

    @PostMapping("/add")
    public ResponseEntity<String> addAirQualityRecord(@RequestBody AirQualityRecordRequest request) {
        airQualityRecordService.addAirQualityRecord(request);
        return ResponseEntity.ok("Air Quality Record added successfully!");
    }
}
