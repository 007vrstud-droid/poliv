package com.example.core;

import com.example.dto.WateringTaskDto;
import com.example.model.Culture;
import com.example.model.CultureObject;
import com.example.repository.CultureObjectRepository;
import com.example.repository.CultureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WateringScheduleService {
    private final CultureRepository cultureRepository;
    private final CultureObjectRepository cultureObjectRepository;

    public List<WateringTaskDto> getTodayWateringTasks() {
        List<WateringTaskDto> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        List<Culture> cultures = cultureRepository.findAll();

        for (Culture culture : cultures) {
            LocalDate lastWatered = culture.getLastWatered();
            int interval = culture.getIntervalDays();

            if (lastWatered == null || !lastWatered.plusDays(interval).isAfter(today)) {
                List<CultureObject> cultureObjects = cultureObjectRepository.findByCultureId(culture.getId());

                for (CultureObject object : cultureObjects) {
                    result.add(new WateringTaskDto(
                            object.getSensorId(),
                            object.getValveId(),
                            culture.getDurationMinutes()
                    ));
                }
                culture.setLastWatered(today);
            }
        }

        return result;
    }
}