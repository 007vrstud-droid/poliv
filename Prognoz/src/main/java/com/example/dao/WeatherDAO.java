package com.example.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class WeatherDAO {
    private final NamedParameterJdbcTemplate jdbc;

    private static final String INSERT_OR_UPDATE_FORECAST_SQL =
        """
        INSERT INTO weather_short (dt_txt, will_rain)
        VALUES (:dt_txt, :will_rain)
        ON CONFLICT (dt_txt) DO UPDATE
        SET will_rain = :will_rain
    """;

    private static final String SELECT_TODAY_FORECAST_SQL =
        """
        SELECT will_rain
        FROM weather_short
        WHERE dt_txt = :dt_txt
    """;

    public void insertTodayForecast(String date, boolean willRain) {
        var params = new MapSqlParameterSource()
                .addValue("dt_txt", date)
                .addValue("will_rain", willRain);

        jdbc.update(INSERT_OR_UPDATE_FORECAST_SQL, params);
    }

    public Boolean getTodayWillRain() {
        String today = LocalDate.now().toString();
        var params = new MapSqlParameterSource("dt_txt", today);
        return jdbc.queryForObject(SELECT_TODAY_FORECAST_SQL, params, Boolean.class);
    }
}