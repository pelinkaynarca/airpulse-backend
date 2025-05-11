package com.airpulse.backend.repository;

import com.airpulse.backend.entity.AirQualityMeasurement;
import com.airpulse.backend.enums.AirQualityParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AirQualityMeasurementRepository extends JpaRepository<AirQualityMeasurement, Long> {

    List<AirQualityMeasurement> findByParameterAndLatitudeBetweenAndLongitudeBetweenAndCreatedAtBetween(
            AirQualityParameter parameter,
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude,
            Instant startTime,
            Instant endTime
    );

    List<AirQualityMeasurement> findByParameter(AirQualityParameter parameter);

    List<AirQualityMeasurement> findByParameterAndCreatedAtBetween(
            AirQualityParameter parameter,
            Instant startTime,
            Instant endTime
    );

    List<AirQualityMeasurement> findByLatitudeBetweenAndLongitudeBetween(
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude
    );

    List<AirQualityMeasurement> findByLatitudeBetweenAndLongitudeBetweenAndCreatedAtBetween(
            Double minLatitude,
            Double maxLatitude,
            Double minLongitude,
            Double maxLongitude,
            Instant startTime,
            Instant endTime
    );

    @Query(value = "SELECT * FROM air_quality_measurement m " +
            "WHERE (ABS(m.latitude - :latitude) <= :radius AND ABS(m.longitude - :longitude) <= :radius) " +
            "ORDER BY " +
            "(POW(m.latitude - :latitude, 2) + POW(m.longitude - :longitude, 2)), " +
            "m.created_at DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<AirQualityMeasurement> findNearestAndLatest(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius);

    @Query("SELECT m FROM AirQualityMeasurement m ORDER BY m.createdAt DESC LIMIT :limit")
    List<AirQualityMeasurement> findLatestMeasurements(@Param("limit") int limit);

    @Query("SELECT AVG(m.value) FROM AirQualityMeasurement m " +
            "WHERE m.parameter = :parameter " +
            "AND m.latitude BETWEEN :minLat AND :maxLat " +
            "AND m.longitude BETWEEN :minLon AND :maxLon " +
            "AND m.createdAt BETWEEN :start AND :end")
    Double calculateAverageForParameterInRegionAndTimeframe(
            @Param("parameter") AirQualityParameter parameter,
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}