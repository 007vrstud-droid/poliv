package com.example.scheduler;

import com.example.properties.WateringProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;

@Slf4j
@Service
public class WateringScheduler {

    private final TaskScheduler taskScheduler;
    private final WateringProperties wateringProperties;
    private final StartWateringTask startWateringTask;
    private final StartManualWateringTask startManualWateringTask;

    public WateringScheduler(TaskScheduler taskScheduler, WateringProperties wateringProperties, StartWateringTask startWateringTask,
                             StartManualWateringTask startManualWateringTask) {
        this.taskScheduler = taskScheduler;
        this.wateringProperties = wateringProperties;
        this.startWateringTask = startWateringTask;
        this.startManualWateringTask = startManualWateringTask;

        scheduleTask();
    }

    public void scheduleTask() {
        if (!wateringProperties.enabled()) {
            log.info("Полив отключён (watering.enabled=false) — планировщик не активен.");
            return;
        }
        boolean isScheduleEnabled = wateringProperties.scheduleCheckEnabled() || wateringProperties.sensorsCheckEnabled();
        boolean isManualEnabled = wateringProperties.manualWatering() != null && wateringProperties.manualWatering().enabled();
        log.info("scheduleCheckEnabled = " + wateringProperties.scheduleCheckEnabled());
        log.info("sensorsCheckEnabled = " + wateringProperties.sensorsCheckEnabled());
        log.info("manualWatering.enabled = " + (wateringProperties.manualWatering() != null ? wateringProperties.manualWatering().enabled() : "null"));

        if (!wateringProperties.scheduleCheckEnabled() && !wateringProperties.sensorsCheckEnabled() && !isManualEnabled) {
            log.info(" Отключены и расписание, и ручной полив — ничего запускать не будем.");
            return;
        }

        // ======= Ручной полив =======
        if (isManualEnabled) {
            log.info("Включен ручной полив");
            String timeStr = wateringProperties.manualWatering().time();
            if (timeStr != null && !timeStr.isBlank()) {
                try {
                    LocalTime time = LocalTime.parse(timeStr);
                    CronTrigger manualCron = new CronTrigger(String.format("0 %d %d * * *", time.getMinute(), time.getHour()));
                    taskScheduler.schedule(startManualWateringTask::startManualWateringTask, manualCron);
                    log.info("Ручной полив ВСЕХ клапанов запланирован на {}", timeStr);
                } catch (Exception e) {
                    log.error(" Невалидное время ручного полива: {}", timeStr);
                }
            }
        } else {
            log.info("Ручной полив отключён (watering.manualWatering.enabled=false).");
        }

        // ======= Обычный полив по расписанию =======
        if (isScheduleEnabled) {
            log.info("Включен автоматический полив");
            String cron = wateringProperties.scheduleCron();
            if (cron == null || cron.isBlank()) {
                log.info("Cron не задан — запускаем обычный полив сразу");
                taskScheduler.schedule(startWateringTask::startWateringTask, Instant.now().plusSeconds(1));
            } else {
                log.info("Запланирован запуск автоматического полива с cron: {}", cron);
                taskScheduler.schedule(startWateringTask::startWateringTask, new CronTrigger(cron));
            }
        }
    }
}