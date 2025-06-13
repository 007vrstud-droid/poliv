package com.example.CalculateMedian;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorsDataRepository extends JpaRepository<SensorsData, Integer> {
    // Получить показание по датчику за указанное время
    List<SensorsData> findBySensorIdAndCreatedAtBetween(Integer sensorId, LocalDateTime start, LocalDateTime end);

    @Query(value = "Select * From data_sensors Where sensor_id = :sensorId and created_at Between :start AND :end", nativeQuery = true)
    List<SensorsData> findBySensorIdAndCreatedAtBetween1(
            @Param("sensorId") Integer sensorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );



}
