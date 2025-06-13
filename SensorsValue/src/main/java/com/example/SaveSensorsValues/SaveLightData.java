package com.example.SaveSensorsValues;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveLightData {
    private final ArduinoSensorValue arduinoSensorValue;
    private final SensorValueDAO sensorValueDAO;

    @Scheduled(fixedDelayString = "#{@appConfig.arduino.pollIntervalSeconds * 1000}")
    public void saveLightData() {
        log.info(" Планировщик запущен: saveLightData()");
        Map<String, Integer> sensorMap = arduinoSensorValue.getDatSvetaValue();
        if (sensorMap == null || sensorMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Integer> entry : sensorMap.entrySet()) {
            String sensorKey = entry.getKey();
            Integer sensorValue = entry.getValue();

            if (sensorValue == null || sensorValue <= 0) {
                log.warn("Игнорируем некорректное значение датчика {}: {}", sensorKey, sensorValue);
                continue;
            }

            try {
                Integer sensorId = parseSensorId(sensorKey);
                if (sensorId == null) {
                    log.warn("Не удалось определить sensor_id из ключа: {}", sensorKey);
                    continue;
                }
                SensorValueDTO dto = new SensorValueDTO(sensorId, sensorValue);
                sensorValueDAO.insertSensorValue(dto);
                log.info("Данные сохранены: {}", dto);
            } catch (Exception e) {
                log.error("Ошибка при вставке данных для {}: {}", sensorKey, e.getMessage(), e);
            }
        }
    }

    private Integer parseSensorId(String sensorKey) {
        if (sensorKey == null) return null;
        if (sensorKey.matches("^sensor\\d+$")) {
            try {
                return Integer.parseInt(sensorKey.substring(6)); // "sensor" длиной 6 символов
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}