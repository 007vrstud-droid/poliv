package com.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "myapp")
public class AppConfig {
    private ArduinoConfig arduino = new ArduinoConfig();
    private StatisticsConfig statistics = new StatisticsConfig();

    @Getter
    @Setter
    public static class ArduinoConfig {
        private int pollIntervalSeconds;
    }

    @Getter
    @Setter
    public static class StatisticsConfig {
        private int medianPeriodSeconds;
    }
}