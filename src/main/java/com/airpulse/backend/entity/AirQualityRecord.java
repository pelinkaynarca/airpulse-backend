package com.airpulse.backend.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "air_quality_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirQualityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "pm2_5", nullable = false)
    private Double pm25; // particulate matter 2.5

    @Column(name = "pm10", nullable = false)
    private Double pm10; // particulate matter 10

    @Column(name = "no2", nullable = false)
    private Double no2; // nitrogen dioxide

    @Column(name = "so2", nullable = false)
    private Double so2; // sulfur dioxide

    @Column(name = "o3", nullable = false)
    private Double o3; // ozone

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

}
