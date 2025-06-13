package com.example.sensors;

import com.example.properties.WateringProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoistureSensorServiceImpl implements MoistureSensorService {

    private final RestTemplate restTemplate;
    private final WateringProperties wateringProperties;

    @Override
    public Map<String, Boolean> checkSoilDryness(List<String> valveIds) {
        try {
            String moistureUrl = wateringProperties.modules().moistureUrl();
            String url = moistureUrl + "/sensors/moisture/check";

            log.info("🔍 Проверяем влажность почвы по {} клапанам", valveIds.size());

            ResponseEntity<Map<String, Boolean>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(valveIds),
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Boolean> result = response.getBody();
            log.info("Ответ от датчиков: {}", result);
            return result != null ? result : Collections.emptyMap();

        } catch (Exception e) {
            log.error("Ошибка при запросе влажности: {}", e.getMessage());
            // В случае ошибки — считаем всё сухим, чтобы не пропустить полив
            return valveIds.stream().collect(Collectors.toMap(v -> v, v -> true));
        }
    }
    @Override
    public List<String> getDryValves(List<Integer> valveNumbers) {
        List<String> valveIds = valveNumbers.stream()
                .map(i -> "v" + i)
                .toList();

        Map<String, Boolean> drynessMap = checkSoilDryness(valveIds);

        return drynessMap.entrySet().stream()
                .filter(Map.Entry::getValue) // isDry == true
                .map(Map.Entry::getKey)
                .toList();
    }




    @Override
    public List<Map<String, Object>> fetchDrySensors(String url) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> body = response.getBody();
            log.info("Ответ от dry-sensors: {}", body);
            return body != null ? body : List.of();
        } catch (Exception e) {
            log.error("Ошибка при запросе dry-sensors: {}", e.getMessage());
            return List.of();
        }
    }
}
