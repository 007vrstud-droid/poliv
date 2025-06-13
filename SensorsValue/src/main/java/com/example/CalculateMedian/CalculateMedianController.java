package com.example.CalculateMedian;

import com.example.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensor-stats")
@RequiredArgsConstructor
public class CalculateMedianController {
    private final CalculateMedian calculateMedian;
    private final AppConfig appConfig;

    @GetMapping("/median")
    public List<SensorDataResponseDTO> getMedian(@RequestParam("names") List<String> names) {
        return names.stream()
                .map(calculateMedian::calculateMedian)
                .filter(dto -> dto != null) // фильтруем возможные null-значения, если датчик не найден
                .collect(Collectors.toList());
    }


    @GetMapping("/dry-sensors")
    public List<SensorMedianCheckDTO> getDrySensors(@RequestParam("names") List<String> names) {
        return names.stream()
                .map(calculateMedian::checkIfMedianAboveThreshold)
                .filter(dto -> dto != null && dto.isAboveThreshold())  // Оставляем только "сухие"
                .collect(Collectors.toList());
    }
}