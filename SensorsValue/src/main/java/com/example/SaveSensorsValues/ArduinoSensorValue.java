package com.example.SaveSensorsValues;

import com.example.properties.ArduinoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArduinoSensorValue {
    private final RestTemplate restTemplate;
    private final ArduinoProperties arduinoProperties;

    public Map<String, Integer> getDatSvetaValue() {
        try {
            Map<String, Integer> sensorValues = restTemplate.getForObject(
                    arduinoProperties.getLight(),
                    Map.class
            );
            log.info("Arduino вернул JSON: {}", sensorValues);
            if (sensorValues == null || sensorValues.isEmpty()) {
                log.warn("Пустой JSON от Arduino");
                return null;
            }
            return sensorValues;
        } catch (Exception e) {
            log.error("Ошибка при получении JSON от Arduino: {}", e.getMessage(), e);
            return null;
        }
    }
}


