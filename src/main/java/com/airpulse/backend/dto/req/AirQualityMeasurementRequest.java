package com.airpulse.backend.dto.req;

import com.airpulse.backend.enums.AirQualityParameter;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.Instant;

public record AirQualityMeasurementRequest(

        @NotNull(message = "Parameter cannot be null")
        AirQualityParameter parameter,

        @PositiveOrZero(message = "Value must be greater than or equal to 0")
        Double value,

        @DecimalMin(value = "-90.0", message = "Latitude must be between -90.0 and 90.0")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90.0 and 90.0")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "Longitude must be between -180.0 and 180.0")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180.0 and 180.0")
        Double longitude

) implements Serializable {
}