    package com.airpulse.backend.entity;

    import com.airpulse.backend.enums.AnomalyType;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.hibernate.annotations.CreationTimestamp;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "anomaly_record")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class AnomalyRecord {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "measurement_id", nullable = false)
        private AirQualityMeasurement measurement;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private AnomalyType anomalyType;

        @Column(nullable = false)
        private Double value;

        private Double referenceValue;

        private Double deviationPercentage;

        @Column(nullable = false)
        @CreationTimestamp
        private LocalDateTime detectedAt;
    }