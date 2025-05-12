package com.airpulse.backend.enums;

import lombok.Getter;

@Getter
public enum AnomalyType {
    WHO_THRESHOLD_EXCEEDED("Exceeds WHO guideline threshold"),
    SUDDEN_INCREASE("50% increase from 24-hour average");
    //SPATIAL_ANOMALY_ZSCORE("Spatial anomaly detected using Z-Score"),
    //TIME_SERIES_ANOMALY("Trend deviation in time series");

    private final String description;

    AnomalyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}