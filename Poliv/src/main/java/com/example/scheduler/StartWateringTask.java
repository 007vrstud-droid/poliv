package com.example.scheduler;

import com.example.arduino.ValveUtils;
import com.example.dto.WateringScheduleItem;
import com.example.properties.WateringProperties;
import com.example.scheduler.service.ScheduleService;
import com.example.scheduler.service.PrognozService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StartWateringTask {

    private final WateringProperties wateringProperties;
    private final TaskScheduler taskScheduler;
    private final PrognozService prognozService;
    private final ScheduleService scheduleService;
    private final WateringService wateringService;

    public void startWateringTask() {
        log.info("Запуск startWateringTask");

        if (prognozService.shouldSkipWateringDueToRain()) {
            log.info("Прогноз дождя — полив отменён.");
            return;
        } else {
            log.info("Прогноз погоды не мешает — продолжаем.");
        }

        boolean scheduleEnabled = wateringProperties.scheduleCheckEnabled();
        boolean sensorsEnabled = wateringProperties.sensorsCheckEnabled();

        log.info("Конфигурация: расписание={}, датчики={}", scheduleEnabled, sensorsEnabled);

        // Вариант 1: и расписание, и датчики включены
        if (scheduleEnabled && sensorsEnabled) {
            log.info("Используем режим: расписание + датчики");
            List<WateringScheduleItem> schedule = scheduleService.fetchTodaySchedule();
            if (schedule.isEmpty()) {
                log.info("Расписание пустое — полив отменён.");
                return;
            }
            wateringService.waterWithScheduleAndSensors(
                    schedule,
                    wateringProperties.sensorsCheckIntervalSeconds()
            );
            return;
        }

        // Вариант 2: только датчики включены
        if (!scheduleEnabled && sensorsEnabled) {
            log.info("Используем режим: только датчики");
            List<String> sensorNames = wateringProperties.manualWatering().valves().stream()
                    .map(ValveUtils::normalizeValveId)
                    .toList();

            wateringService.waterWithSensorsOnlyByDryness(
                    sensorNames,
                    wateringProperties.sensorsCheckIntervalSeconds()
            );
            return;
        }

        // Вариант 3: только расписание включено
        if (scheduleEnabled) {
            log.info("Используем режим: только расписание");
            List<WateringScheduleItem> schedule = scheduleService.fetchTodaySchedule();
            if (schedule.isEmpty()) {
                log.info("Расписание пустое — полив отменён.");
                return;
            }
            wateringService.waterWithoutSensors(schedule);
            return;
        }
        // Если ни один режим не активен
        log.warn("Отключены оба модуля — полив не запускается.");
    }
}