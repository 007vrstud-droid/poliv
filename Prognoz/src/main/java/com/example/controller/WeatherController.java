package com.example.controller;

import com.example.core.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/will-it-rain")
    public Boolean willItRain() {
        return weatherService.willRainToday();
    }
}