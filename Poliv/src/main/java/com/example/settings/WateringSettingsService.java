package com.example.settings;

import com.example.properties.WateringProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class WateringSettingsService {

    private WateringProperties current;

    public synchronized WateringProperties getSettings() {
        return current;
    }

    public synchronized void updateManualSettings(boolean enabled, String time, int durationMinutes, List<String> valves) {
        boolean autoEnabled = enabled ? false : current.enabled();

        var updatedManual = new WateringProperties.ManualWatering(enabled, time, durationMinutes, valves);

        this.current = new WateringProperties(
                autoEnabled,
                current.scheduleCheckEnabled(),
                current.prognozCheckEnabled(),
                current.sensorsCheckEnabled(),
                current.sensorsCheckIntervalSeconds(),
                current.scheduleCron(),
                current.modules(),
                updatedManual
        );

        if (enabled) {
            log.info("Включён ручной полив: время = {}, длительность = {} мин., клапаны = {}", time, durationMinutes, valves);
            log.info("Автоматический режим выключен");
        } else {
            log.info("Ручной полив выключен");
        }
    }

    public synchronized void updateAutoSettings(
            boolean enabled,
            boolean scheduleCheckEnabled,
            boolean prognozCheckEnabled,
            boolean sensorsCheckEnabled,
            int sensorsCheckIntervalSeconds,
            String scheduleCron
    ) {
        var manual = current.manualWatering();
        var updatedManual = enabled
                ? new WateringProperties.ManualWatering(false, manual.time(), manual.durationMinutes(), manual.valves())
                : manual;

        this.current = new WateringProperties(
                enabled,
                scheduleCheckEnabled,
                prognozCheckEnabled,
                sensorsCheckEnabled,
                sensorsCheckIntervalSeconds,
                scheduleCron,
                current.modules(),
                updatedManual
        );

        if (enabled) {
            log.info("Включён автоматический полив:");
            log.info("   - Расписание: {}", scheduleCron);
            log.info("   - Прогноз погоды: {}", prognozCheckEnabled);
            log.info("   - Сенсоры почвы: {}", sensorsCheckEnabled);
            log.info("   - Интервал сенсоров: {} сек", sensorsCheckIntervalSeconds);
            log.info(" Ручной режим выключен");
        } else {
            log.info(" Автоматический полив выключен");
        }
    }
}