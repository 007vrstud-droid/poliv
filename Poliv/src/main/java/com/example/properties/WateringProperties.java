package com.example.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "watering")
public record WateringProperties(
        boolean enabled,                     // Включена ли система полива в целом
        boolean scheduleCheckEnabled,        // Включено ли расписание полива
        boolean prognozCheckEnabled,         // Проверять ли погодные условия перед поливом
        boolean sensorsCheckEnabled,           // Включена ли проверка влажности почвы
        int sensorsCheckIntervalSeconds,       // Интервал проверки влажности в секундах
        String scheduleCron,                 // CRON-выражение для расписания полива
        ModuleUrls modules,                  // URL-адреса модулей
        ManualWatering manualWatering     // Настройки ручного полива

) {

    public record ManualWatering(
            boolean enabled,
            String time,
            int durationMinutes,
            List<String> valves
    ) {
        public ManualWatering {
            if (time == null) time = "06:00";
            if (durationMinutes == 0) durationMinutes = 120;
            if (valves == null) valves = List.of();
        }
    }

    public record ModuleUrls(
            String prognozUrl,         // URL для получения погодных данных
            String scheduleUrl,        // URL сервиса расписания
            String sensorsUrl,         // URL для сенсоров (например, влажности)
            String arduinoUrl,         // Базовый URL для отправки команд на Arduino
            String moistureUrl)
    {}
}
