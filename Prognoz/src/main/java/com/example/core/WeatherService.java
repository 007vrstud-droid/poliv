package com.example.core;
import com.example.Properties.WeatherProperties;
import com.example.dao.WeatherDAO;
import com.example.dto.ForecastDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final WeatherDAO dao;
    private final RestTemplate restTemplate;
    private final WeatherProperties properties;

    private static final String LAT = "51.601515";
    private static final String LON = "39.284531";
    private static final String UNITS = "metric";
    private static final String LANG = "ru";

    @Scheduled(cron = "30 53 14 * * *")
    public void fetchAndSaveTodayForecast() {
        String url = String.format(
                "%sdata/2.5/forecast?appid=%s&lat=%s&lon=%s&units=%s&lang=%s",
                properties.getUrl(), properties.getApiKey(), LAT, LON, UNITS, LANG
        );

        try {
            ResponseEntity<ForecastDto> response =
                    restTemplate.getForEntity(url, ForecastDto.class);
            ForecastDto dto = response.getBody();

            if (dto != null && dto.items() != null) {
                String today = LocalDate.now().toString();
                double threshold = properties.getRainThreshold();

                boolean willRain = dto.items().stream()
                        .filter(i -> i.dtTxt().startsWith(today))
                        .anyMatch(i ->
                                (i.pop() != null && i.pop() >= threshold) ||
                                        (i.weather() != null && !i.weather().isEmpty() &&
                                                "Rain".equalsIgnoreCase(i.weather().get(0).main()))
                        );

                dao.insertTodayForecast(today, willRain);
                log.info("Forecast for today saved: willRain = {}", willRain);
            } else {
                log.warn("Empty forecast data.");
            }
        } catch (Exception e) {
            log.error("Error fetching weather: {}", e.getMessage(), e);
        }
    }

    public Boolean willRainToday() {
        return dao.getTodayWillRain();
    }
}