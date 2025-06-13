package com.example.dto;

public record WateringTaskDto(
        Integer sensorId,
        String valveId,
        Integer durationMinutes
) {}
