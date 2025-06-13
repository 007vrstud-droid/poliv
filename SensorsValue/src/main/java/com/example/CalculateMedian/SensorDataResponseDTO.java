package com.example.CalculateMedian;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SensorDataResponseDTO (
    @NotNull
    String name,
    @NotNull
    BigDecimal sensorValue){}

