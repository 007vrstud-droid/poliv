package com.example.dto;

public record WateringScheduleItem(
        int sensorId,
        String valveId,
        int durationMinutes,
        String startTime, // формат "HH:mm"
        String endTime    // формат "HH:mm"
) {}