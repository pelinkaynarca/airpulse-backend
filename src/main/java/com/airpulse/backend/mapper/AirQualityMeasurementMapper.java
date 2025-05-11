package com.airpulse.backend.mapper;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.entity.AirQualityMeasurement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AirQualityMeasurementMapper {

    AirQualityMeasurementMapper INSTANCE = Mappers.getMapper(AirQualityMeasurementMapper.class);

    AirQualityMeasurement mapRequestToEntity(AirQualityMeasurementRequest request);

    AirQualityMeasurementResponse mapEntityToResponse(AirQualityMeasurement measurement);

}
