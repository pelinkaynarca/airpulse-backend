package com.airpulse.backend.mapper;

import com.airpulse.backend.dto.res.AnomalyRecordResponse;
import com.airpulse.backend.entity.AirQualityMeasurement;
import com.airpulse.backend.entity.AnomalyRecord;
import com.airpulse.backend.enums.AnomalyType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnomalyRecordMapper {
    AnomalyRecordMapper INSTANCE = Mappers.getMapper(AnomalyRecordMapper.class);

    @Mapping(source = "measurement.id", target = "measurementId")
    @Mapping(source = "measurement.parameter", target = "parameter")
    @Mapping(source = "measurement.latitude", target = "latitude")
    @Mapping(source = "measurement.longitude", target = "longitude")
    @Mapping(source = "anomalyType", target = "anomalyType")
    AnomalyRecordResponse mapEntityToResponse(AnomalyRecord anomalyRecord);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "detectedAt", expression = "java(LocalDateTime.now())")
    @Mapping(source = "measurement", target = "measurement")
    @Mapping(source = "anomalyType", target = "anomalyType")
    @Mapping(source = "measurement.value", target = "value")
    AnomalyRecord mapMeasurementToAnomaly(AirQualityMeasurement measurement, AnomalyType anomalyType);
}