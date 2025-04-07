package com.airpulse.backend.mapper;

import com.airpulse.backend.dto.AirQualityRecordRequest;
import com.airpulse.backend.entity.AirQualityRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AirQualityRecordMapper {

    AirQualityRecord toEntity(AirQualityRecordRequest request);

}
