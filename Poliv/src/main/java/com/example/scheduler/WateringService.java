package com.example.scheduler;

import com.example.arduino.ArduinoService;
import com.example.dto.WateringScheduleItem;
import com.example.properties.WateringProperties;
import com.example.sensors.MoistureSensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WateringService {

    private final ArduinoService arduinoService;
    private final MoistureSensorService moistureSensorService;
    private final TaskScheduler taskScheduler;
    private final WateringProperties wateringProperties;

    private static final long MAX_DURATION_MILLIS = Duration.ofHours(4).toMillis();
    private ScheduledFuture<?> sensorTask;

    private final Map<String, Instant> openValveTimestamps = new HashMap<>();
    private final Set<String> openValves = new HashSet<>();

    // ========== Режим 1: Расписание + Датчики ==========
    public void waterWithScheduleAndSensors(List<WateringScheduleItem> schedule, int intervalSeconds) {
        log.info("🚿 Старт полива: Режим 'расписание + датчики'");

        Map<String, String> sensorToValve = schedule.stream()
                .collect(Collectors.toMap(
                        item -> "sensor" + item.sensorId(),
                        WateringScheduleItem::valveId
                ));

        Set<String> sensorNames = sensorToValve.keySet();
        Instant start = Instant.now();

        sensorTask = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                // Прерывание по таймеру
                if (Duration.between(start, Instant.now()).toMillis() > MAX_DURATION_MILLIS) {
                    log.warn("Превышено 4 часа — закрываем всё");
                    closeAllValves();
                    cancelSensorTask();
                    return;
                }

                Set<String> drySensors = fetchDrySensors(sensorNames);
                Set<String> dryValves = drySensors.stream()
                        .map(sensorToValve::get)
                        .collect(Collectors.toSet());

                // Открываем новые
                for (String valveId : dryValves) {
                    openValveIfNeeded(valveId);
                }

                // Закрываем, если увлажнилось
                for (String valveId : new HashSet<>(openValves)) {
                    if (!dryValves.contains(valveId)) {
                        closeValve(valveId);
                    }
                }

                if (dryValves.isEmpty()) {
                    log.info("Все сенсоры увлажнены — завершаем");
                    closeAllValves();
                    cancelSensorTask();
                }

            } catch (Exception e) {
                log.error(" Ошибка в задаче опроса сенсоров: {}", e.getMessage(), e);
            }
        }, Duration.ofSeconds(intervalSeconds));
    }

    // ========== Режим 2: Только Расписание ==========
    public void waterWithoutSensors(List<WateringScheduleItem> schedule) {
        log.info("🚿 Старт полива: Режим 'только расписание'");

        for (WateringScheduleItem item : schedule) {
            String valveId = item.valveId();
            int duration = item.durationMinutes();

            log.info("Открываем клапан {} на {} мин", valveId, duration);
            openValveIfNeeded(valveId);

            taskScheduler.schedule(() -> {
                log.info(" Закрываем клапан {} по таймеру", valveId);
                closeValve(valveId);
            }, Instant.now().plus(Duration.ofMinutes(duration)));
        }
    }

    // ========== Режим 3: Только Датчики ==========
    public void waterWithSensorsOnlyByDryness(List<String> sensorNames, int intervalSeconds) {
        log.info("Старт полива: Режим 'только датчики'");

        Instant start = Instant.now();
        sensorTask = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                if (Duration.between(start, Instant.now()).toMillis() > MAX_DURATION_MILLIS) {
                    log.warn(" Лимит 4 часа — завершаем");
                    closeAllValves();
                    cancelSensorTask();
                    return;
                }

                Set<String> drySensors = fetchDrySensors(new HashSet<>(sensorNames));
                Set<String> dryValves = drySensors.stream()
                        .map(this::mapSensorToValve)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                // Открываем новые
                for (String valveId : dryValves) {
                    openValveIfNeeded(valveId);
                }

                // Закрываем лишние
                for (String valveId : new HashSet<>(openValves)) {
                    if (!dryValves.contains(valveId)) {
                        closeValve(valveId);
                    }
                }

                if (dryValves.isEmpty()) {
                    log.info(" Все сенсоры увлажнены — завершение");
                    closeAllValves();
                    cancelSensorTask();
                }

            } catch (Exception e) {
                log.error(" Ошибка в поливе по датчикам: {}", e.getMessage(), e);
            }

        }, Duration.ofSeconds(intervalSeconds));
    }

    // ========== Управление клапанами ==========
    private void openValveIfNeeded(String valveId) {
        if (!openValves.contains(valveId)) {
            log.info(" Открываем клапан {}", valveId);
            arduinoService.openValve(List.of(new WateringScheduleItem(0, valveId, 0, "00:00", "00:00")));
            openValves.add(valveId);
            openValveTimestamps.put(valveId, Instant.now());
        }
    }

    private void closeValve(String valveId) {
        if (openValves.contains(valveId)) {
            log.info(" Закрываем клапан {}", valveId);
            arduinoService.closeValve(List.of(new WateringScheduleItem(0, valveId, 0, "00:00", "00:00")));
            openValves.remove(valveId);
            openValveTimestamps.remove(valveId);
        }
    }

    private void closeAllValves() {
        for (String valveId : new HashSet<>(openValves)) {
            closeValve(valveId);
        }
    }

    private void cancelSensorTask() {
        if (sensorTask != null && !sensorTask.isCancelled()) {
            sensorTask.cancel(false);
        }
    }

    // ========== Утилиты ==========
    private Set<String> fetchDrySensors(Set<String> sensorNames) {
        if (sensorNames.isEmpty()) return Set.of();
        String query = sensorNames.stream()
                .map(name -> "names=" + name)
                .collect(Collectors.joining("&"));
        String url = "http://localhost:8083/sensor-stats/dry-sensors?" + query;
        List<Map<String, Object>> response = moistureSensorService.fetchDrySensors(url);

        return response.stream()
                .map(sensor -> (String) sensor.get("name"))
                .collect(Collectors.toSet());
    }

    private String mapSensorToValve(String sensorName) {
        // Пример: sensor1 → v1
        if (sensorName.startsWith("sensor")) {
            try {
                int id = Integer.parseInt(sensorName.substring(6));
                return "v" + id;
            } catch (NumberFormatException e) {
                log.warn("Неверный формат sensorName: {}", sensorName);
            }
        }
        return null;
    }
}
