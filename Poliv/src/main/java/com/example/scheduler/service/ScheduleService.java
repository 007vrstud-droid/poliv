package com.example.scheduler.service;

import com.example.dto.WateringScheduleItem;
import com.example.properties.WateringProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final RestTemplate restTemplate;
    private final WateringProperties properties;

    public List<WateringScheduleItem> fetchTodaySchedule() {
        String url = properties.modules().scheduleUrl();
        try {
            List<WateringScheduleItem> schedule = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<WateringScheduleItem>>() {
                    }
            ).getBody();

            log.info("Получено {} задач(и) из модуля расписания", schedule != null ? schedule.size() : 0);
            return schedule != null ? schedule : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Ошибка при получении расписания полива: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}