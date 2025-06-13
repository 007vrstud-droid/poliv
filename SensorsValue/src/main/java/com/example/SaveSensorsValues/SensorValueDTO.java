package com.example.SaveSensorsValues;

import jakarta.validation.constraints.NotNull;

public record SensorValueDTO(
        @NotNull(message = "Error sensor_id=null")
        Integer   sensor_id,
        @NotNull(message = "Error sensor_value=null")
        Integer sensor_value
) {}


