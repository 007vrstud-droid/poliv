package com.example.CalculateMedian;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SensorMedianCheckDTO(
        @NotNull String name,
        @NotNull BigDecimal median,
        boolean isAboveThreshold
) {}