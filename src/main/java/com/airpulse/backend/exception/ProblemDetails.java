package com.airpulse.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProblemDetails {
    private final int status;
    private final String message;
    private final String detail;
}