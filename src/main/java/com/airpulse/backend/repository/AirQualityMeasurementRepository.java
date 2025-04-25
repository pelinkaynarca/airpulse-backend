package com.airpulse.backend.repository;

import com.airpulse.backend.entity.AirQualityMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirQualityMeasurementRepository extends JpaRepository<AirQualityMeasurement, Long> {
    @Query(value = """
        SELECT *
        FROM air_quality_measurement
        WHERE earth_distance(ll_to_earth(:latitude, :longitude), ll_to_earth(latitude, longitude)) <= 25000
        ORDER BY timestamp DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<AirQualityMeasurement> findNearestAndLatest(@Param("latitude") double latitude,
                                                         @Param("longitude") double longitude);
}