package com.example.controller;

import com.example.core.WateringScheduleService;
import com.example.dto.WateringTaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class WateringScheduleController {

    private final WateringScheduleService wateringScheduleService;

    @GetMapping("/today")
    public List<WateringTaskDto> getTodayWateringTasks() {
        return wateringScheduleService.getTodayWateringTasks();
    }
}