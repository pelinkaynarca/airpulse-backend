package com.airpulse.backend.service;

import com.airpulse.backend.dto.req.AirQualityMeasurementRequest;
import com.airpulse.backend.dto.res.AirQualityMeasurementResponse;
import com.airpulse.backend.enums.AirQualityParameter;

import java.time.Instant;
import java.util.List;

public interface AirQualityMeasurementService {

    List<AirQualityMeasurementResponse> getAllMeasurements();

    void addAirQualityMeasurement(AirQualityMeasurementRequest request);

    AirQualityMeasurementResponse getNearestAndLatestMeasurement(double latitude, double longitude);

    // startTime and endTime can be null
    List<AirQualityMeasurementResponse> getMeasurementsByParameter(
            AirQualityParameter parameter,
            Instant startTime,
            Instant endTime
    );

    // startTime and endTime can be null
    List<AirQualityMeasurementResponse> getMeasurementsInRegion(
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude,
            Instant startTime,
            Instant endTime
    );

    List<AirQualityMeasurementResponse> getLatestMeasurements(int limit);

    Double calculateAverageForParameterInRegionAndTimeframe(
            AirQualityParameter parameter,
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude,
            Instant startTime,
            Instant endTime
    );
}