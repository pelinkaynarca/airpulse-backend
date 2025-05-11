package com.airpulse.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AirQualityParameter {
    PM2_5("PM2.5"),
    PM10("PM10"),
    NO2("NO2"),
    SO2("SO2"),
    O3("O3");

    @JsonValue
    private final String value;

    @JsonCreator
    public static AirQualityParameter fromString(String value) {
        if (value == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(param -> param.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid parameter: " + value + ". Valid parameters are: " +
                                Arrays.toString(Arrays.stream(values()).map(p -> p.value).toArray())
                ));
    }

    @Override
    public String toString() {
        return value;
    }
}