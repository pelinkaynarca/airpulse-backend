package com.airpulse.backend.mapper;

import com.airpulse.backend.dto.AirQualityMeasurementRequest;
import com.airpulse.backend.entity.AirQualityMeasurement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirQualityMeasurementMapper {

    AirQualityMeasurement toEntity(AirQualityMeasurementRequest request);

}
