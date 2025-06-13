package com.example.arduino;

import com.example.dto.WateringScheduleItem;
import com.example.properties.WateringProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class ArduinoServiceImpl implements ArduinoService {

    private final RestTemplate restTemplate;
    private final WateringProperties wateringProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void openValve(List<WateringScheduleItem> schedule) {
        handleValveCommand(schedule, "/valves/open", "Открыты");
    }

    @Override
    public void closeValve(List<WateringScheduleItem> schedule) {
        handleValveCommand(schedule, "/valves/close", "Закрыты");
    }

    private void handleValveCommand(List<WateringScheduleItem> schedule, String endpoint, String successLogPrefix) {
        List<Integer> availableValves = getAvailableValves();
        log.info("🔍 Доступные клапаны Arduino: {}", availableValves);

        if (availableValves.isEmpty()) {
            log.warn("Список доступных клапанов пуст — операция отменена.");
            return;
        }

        List<String> valveIds = getValidValveIds(schedule, availableValves);
        if (valveIds.isEmpty()) {
            log.warn("Ни один клапан из расписания не найден среди доступных.");
            return;
        }

        sendValveCommand(endpoint, valveIds, successLogPrefix);
    }

    private List<String> getValidValveIds(List<WateringScheduleItem> schedule, List<Integer> availableValves) {
        return schedule.stream()
                .map(WateringScheduleItem::valveId)
                .map(ValveUtils::normalizeValveId)
                .filter(valveId -> {
                    try {
                        return availableValves.contains(Integer.valueOf(valveId));
                    } catch (NumberFormatException e) {
                        log.warn("Невалидный valveId '{}', пропускаем", valveId);
                        return false;
                    }
                })
                .toList();
    }

    private void sendValveCommand(String endpoint, List<String> valveIds, String logPrefix) {
        String url = wateringProperties.modules().arduinoUrl() + endpoint;
        try {
            restTemplate.postForLocation(url, valveIds);
            log.info("{} клапаны {}", logPrefix, valveIds);
        } catch (Exception e) {
            log.error("Ошибка при запросе к {}: {}", url, e.getMessage());
        }
    }


    public List<Integer> getAvailableValves() {
        String url = wateringProperties.modules().arduinoUrl() + "/valves/status";
        try {
            String json = restTemplate.getForObject(url, String.class);
            log.debug("📥 Ответ от Arduino: {}", json);

            Map<String, String> valvesStatus = objectMapper.readValue(json, new TypeReference<>() {});
            List<Integer> available = valvesStatus.keySet().stream()
                    .map(Integer::valueOf)
                    .toList();

            log.info("🔍 Доступные клапаны Arduino: {}", available);
            return available;

        } catch (Exception e) {
            log.error("Ошибка при получении статуса клапанов с Arduino: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
