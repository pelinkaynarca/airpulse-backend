package com.airpulse.backend.service;

import com.airpulse.backend.dto.AirQualityRecordRequest;

public interface AirQualityRecordService {
    void addAirQualityRecord(AirQualityRecordRequest request);
}
