package com.example.scheduler;

import com.example.properties.WateringProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class StartManualWateringTask {

    private final WateringProperties wateringProperties;
    private final RestTemplate restTemplate;
    private final TaskScheduler taskScheduler;


    public void startManualWateringTask() {
        String baseUrl = wateringProperties.modules().arduinoUrl();
        int duration = wateringProperties.manualWatering().durationMinutes();
        List<String> valveIds = wateringProperties.manualWatering().valves();

        if (valveIds == null || valveIds.isEmpty()) {
            log.warn("Список клапанов для ручного полива пуст, ничего не делаем.");
            return;
        }
        log.info("Запуск ручного полива — клапаны [{}] открываем на {} минут",
                String.join(", ", valveIds), duration);
        //открытие клапанов
        try {
            String openUrl = baseUrl + "/valves/open";
            restTemplate.postForLocation(openUrl, valveIds);
            log.info("Открыты клапаны: {}", valveIds);
        } catch (Exception e) {
            log.error("Ошибка при открытии клапанов: {}", e.getMessage());
            return;
        }

        //закрытие клапанов
        taskScheduler.schedule(
                () -> {
                    try {
                        String closeUrl = baseUrl + "/valves/close";
                        restTemplate.postForLocation(closeUrl, valveIds);
                        log.info("Закрыты клапаны: {}", valveIds);
                    } catch (Exception e) {
                        log.error("Ошибка при закрытии клапанов: {}", e.getMessage());
                    }
                },
                Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(duration))
        );
    }
}

