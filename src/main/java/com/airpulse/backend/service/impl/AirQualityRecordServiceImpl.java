package com.airpulse.backend.service.impl;

import com.airpulse.backend.dto.AirQualityRecordRequest;
import com.airpulse.backend.entity.AirQualityRecord;
import com.airpulse.backend.mapper.AirQualityRecordMapper;
import com.airpulse.backend.repository.AirQualityRecordRepository;
import com.airpulse.backend.service.AirQualityRecordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AirQualityRecordServiceImpl implements AirQualityRecordService {

    private final AirQualityRecordRepository airQualityRecordRepository;
    private final AirQualityRecordMapper airQualityRecordMapper;

    @Override
    public void addAirQualityRecord(AirQualityRecordRequest request) {
        AirQualityRecord airQualityRecord = airQualityRecordMapper.toEntity(request);
        airQualityRecordRepository.save(airQualityRecord);
    }
}