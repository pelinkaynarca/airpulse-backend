package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.AirQualityMeasurementRequest;
import com.airpulse.backend.entity.AirQualityMeasurement;
import com.airpulse.backend.mapper.AirQualityMeasurementMapper;
import com.airpulse.backend.repository.AirQualityMeasurementRepository;
import com.airpulse.backend.service.AirQualityMeasurementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AirQualityMeasurementServiceImpl implements AirQualityMeasurementService {

    private final AirQualityMeasurementRepository airQualityMeasurementRepository;
    private final AirQualityMeasurementMapper airQualityMeasurementMapper;

    @Override
    public void addAirQualityMeasurement(AirQualityMeasurementRequest request) {
        AirQualityMeasurement airQualityMeasurement = airQualityMeasurementMapper.toEntity(request);
        airQualityMeasurementRepository.save(airQualityMeasurement);
    }
}