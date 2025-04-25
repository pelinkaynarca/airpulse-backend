package com.airpulse.backend.dto.req;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record AirQualityMeasurementRequest(

        @DecimalMin(value = "-90.0", message = "Latitude must be between -90.0 and 90.0")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90.0 and 90.0")
        double latitude,

        @DecimalMin(value = "-180.0", message = "Longitude must be between -180.0 and 180.0")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180.0 and 180.0")
        double longitude,

        @PositiveOrZero(message = "PM2.5 must be greater than or equal to 0")
        double pm25,

        @PositiveOrZero(message = "PM10 must be greater than or equal to 0")
        double pm10,

        @PositiveOrZero(message = "NO2 must be greater than or equal to 0")
        double no2,

        @PositiveOrZero(message = "SO2 must be greater than or equal to 0")
        double so2,

        @PositiveOrZero(message = "O3 must be greater than or equal to 0")
        double o3,

        @NotNull(message = "Timestamp cannot be null")
        LocalDateTime timestamp

) {
}
