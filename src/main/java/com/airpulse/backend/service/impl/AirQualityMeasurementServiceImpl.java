package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.entity.AirQualityMeasurement;
import com.airpulse.backend.exception.ResourceNotFoundException;
import com.airpulse.backend.mapper.AirQualityMeasurementMapper;
import com.airpulse.backend.repository.AirQualityMeasurementRepository;
import com.airpulse.backend.service.AirQualityMeasurementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AirQualityMeasurementServiceImpl implements AirQualityMeasurementService {

    private final AirQualityMeasurementRepository airQualityMeasurementRepository;
    private final AirQualityMeasurementMapper airQualityMeasurementMapper;

    @Override
    public AirQualityMeasurementResponse getNearestAndLatestMeasurement(double latitude, double longitude) {
        AirQualityMeasurement airQualityMeasurement = airQualityMeasurementRepository.findNearestAndLatest(latitude, longitude)
                .orElseThrow(() -> new ResourceNotFoundException("No recent air quality data found near the given location."));
        return airQualityMeasurementMapper.toResponse(airQualityMeasurement);
    }

    @Override
    public List<AirQualityMeasurementResponse> getAllMeasurements() {
        List<AirQualityMeasurement> entities = airQualityMeasurementRepository.findAll();
        return entities.stream()
                .map(airQualityMeasurementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void addAirQualityMeasurement(AirQualityMeasurementRequest request) {
        AirQualityMeasurement airQualityMeasurement = airQualityMeasurementMapper.toEntity(request);
        airQualityMeasurementRepository.save(airQualityMeasurement);
    }
}