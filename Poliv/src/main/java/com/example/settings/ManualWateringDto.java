package com.example.settings;


import java.util.List;

public record ManualWateringDto(
        boolean enabled,
        String time,
        int durationMinutes,
        List<String> valves
) {}

