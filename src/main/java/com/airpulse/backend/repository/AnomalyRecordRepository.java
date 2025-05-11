package com.airpulse.backend.repository;

import com.airpulse.backend.entity.AnomalyRecord;
import com.airpulse.backend.enums.AnomalyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AnomalyRecordRepository extends JpaRepository<AnomalyRecord, Long> {

    List<AnomalyRecord> findByDetectedAtBetween(Instant start, Instant end);

    List<AnomalyRecord> findByAnomalyType(AnomalyType anomalyType);

    @Query("SELECT a FROM AnomalyRecord a ORDER BY a.detectedAt DESC LIMIT :limit")
    List<AnomalyRecord> findTopByOrderByDetectedAtDesc(@Param("limit") int limit);

    @Query("SELECT a FROM AnomalyRecord a JOIN a.measurement m " +
            "WHERE m.latitude BETWEEN :minLat AND :maxLat " +
            "AND m.longitude BETWEEN :minLon AND :maxLon")
    List<AnomalyRecord> findInRegion(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon);

    @Query("SELECT a FROM AnomalyRecord a JOIN a.measurement m " +
            "WHERE m.latitude BETWEEN :minLat AND :maxLat " +
            "AND m.longitude BETWEEN :minLon AND :maxLon " +
            "AND a.detectedAt BETWEEN :start AND :end")
    List<AnomalyRecord> findInRegionWithTimeframe(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon,
            @Param("start") Instant start,
            @Param("end") Instant end);

    @Query("SELECT a FROM AnomalyRecord a JOIN a.measurement m " +
            "WHERE m.parameter = :parameter")
    List<AnomalyRecord> findByParameter(@Param("parameter") String parameter);

    @Query("SELECT a FROM AnomalyRecord a JOIN a.measurement m " +
            "WHERE m.parameter = :parameter " +
            "AND a.detectedAt BETWEEN :start AND :end")
    List<AnomalyRecord> findByParameterAndTimeframe(
            @Param("parameter") String parameter,
            @Param("start") Instant start,
            @Param("end") Instant end);
}