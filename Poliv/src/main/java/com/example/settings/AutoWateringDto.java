package com.example.settings;

public record AutoWateringDto(
        boolean enabled,
        boolean scheduleCheckEnabled,
        boolean prognozCheckEnabled,
        boolean sensorsCheckEnabled,
        int sensorsCheckIntervalSeconds,
        String scheduleCron
) {}