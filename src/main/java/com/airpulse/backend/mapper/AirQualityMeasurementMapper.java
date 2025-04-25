package com.airpulse.backend.mapper;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.entity.AirQualityMeasurement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirQualityMeasurementMapper {

    AirQualityMeasurement toEntity(AirQualityMeasurementRequest request);

    AirQualityMeasurementResponse toResponse(AirQualityMeasurement measurement);

}
