package com.example.SaveSensorsValues;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SensorValueDAO {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final static String sqlInsertSensorValue="""
    INSERT INTO public.data_sensors (sensor_id,sensor_value)
    VALUES (:sensor_id,:sensor_value)""";

    public void insertSensorValue(SensorValueDTO sensorValueDTO){
        MapSqlParameterSource parameterSource=new MapSqlParameterSource();
        parameterSource.addValue("sensor_id", sensorValueDTO.sensor_id());
        parameterSource.addValue("sensor_value", sensorValueDTO.sensor_value());
        int insertInt= jdbcTemplate.update(sqlInsertSensorValue,parameterSource);
        log.info("insertSensorValue Сделали запись в таблицу {}",insertInt);
    }
}
