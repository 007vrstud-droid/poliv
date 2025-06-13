package com.example.CalculateMedian;

import com.example.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;

@Component
@AllArgsConstructor
@Slf4j
public class CalculateMedian {

    private final SensorsRepository sensorsRepository;
    private final SensorsDataRepository sensorsDataRepository;
    private final AppConfig appConfig;

    public SensorDataResponseDTO calculateMedian(String name) {
        int defaultPeriod = appConfig.getStatistics().getMedianPeriodSeconds();
        return calculateMedian(name, defaultPeriod);
    }

    @Cacheable(value = "medianSensorData", key = "#name + '-' + #temp")
    public SensorDataResponseDTO calculateMedian(String name, Integer temp) {
        log.info("Вызов метода calculateMedian с параметрами: name={}, temp={}",name,temp);

        Optional<Sensors> sensorOpt = sensorsRepository.findByName(name);
        if (sensorOpt.isEmpty()) {return null;}

        Sensors sensor = sensorOpt.get();

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusSeconds(temp);

        List<SensorsData> listData = sensorsDataRepository
                .findBySensorIdAndCreatedAtBetween(sensor.getId(), start, end);

        if (listData.isEmpty()) { return null;}


        listData.sort(Comparator.comparing(SensorsData::getSensorValue));
        BigDecimal medianValue;
        int size = listData.size();

        if (size % 2 == 1) {
            medianValue = listData.get(size / 2).getSensorValue();
        } else {
            BigDecimal val1 = listData.get(size / 2 - 1).getSensorValue();
            BigDecimal val2 = listData.get(size / 2).getSensorValue();
            medianValue = val1.add(val2).divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP);
        }

        return new SensorDataResponseDTO(sensor.getName(),medianValue);
    }












    public SensorMedianCheckDTO checkIfMedianAboveThreshold(String name) {
        int defaultPeriod = appConfig.getStatistics().getMedianPeriodSeconds();
        return checkIfMedianAboveThreshold(name, defaultPeriod);
    }

    public SensorMedianCheckDTO checkIfMedianAboveThreshold(String name, Integer period) {
        SensorDataResponseDTO medianDTO = calculateMedian(name, period);
        if (medianDTO == null) {
            return null;
        }

        Optional<Sensors> sensorOpt = sensorsRepository.findByName(name);
        if (sensorOpt.isEmpty()) return null;

        Sensors sensor = sensorOpt.get();
        Integer maxHumidity = sensor.getHumidity();

        boolean isAboveThreshold = maxHumidity != null &&
                medianDTO.sensorValue().compareTo(BigDecimal.valueOf(maxHumidity)) > 0;

        return new SensorMedianCheckDTO(
                sensor.getName(),
                medianDTO.sensorValue(),
                isAboveThreshold
        );
    }













}