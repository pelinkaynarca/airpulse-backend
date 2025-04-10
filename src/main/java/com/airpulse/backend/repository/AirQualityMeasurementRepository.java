package com.airpulse.backend.repository;

import com.airpulse.backend.entity.AirQualityMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirQualityMeasurementRepository extends JpaRepository<AirQualityMeasurement, Long> {
}