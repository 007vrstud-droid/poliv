package com.example.scheduler.service;

import com.example.properties.WateringProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrognozService {

    private final RestTemplate restTemplate;
    private final WateringProperties wateringProperties;

    public boolean shouldSkipWateringDueToRain() {
        if(wateringProperties.prognozCheckEnabled()){
            log.info(" Включен прогноз погоды watering:weather-check-enabled: true");
        } else {log.info(" Включен прогноз погоды watering:weather-check-enabled: false");
            return false;
        }

        String url = wateringProperties.modules().prognozUrl();
        try {
            Boolean willRain = restTemplate.getForObject(url, Boolean.class);
            log.info("Прогноз погоды: будет дождь = {}", willRain);
            return Boolean.TRUE.equals(willRain);
        } catch (Exception e) {
            log.warn("Ошибка при запросе прогноза погоды: {}", e.getMessage());
            return false;
        }
    }
}