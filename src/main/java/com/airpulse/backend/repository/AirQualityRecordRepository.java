package com.airpulse.backend.repository;

import com.airpulse.backend.entity.AirQualityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirQualityRecordRepository extends JpaRepository<AirQualityRecord, Long> {
}