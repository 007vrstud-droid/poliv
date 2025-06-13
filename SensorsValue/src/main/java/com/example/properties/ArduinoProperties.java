package com.example.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "arduino")
@Data
        public class ArduinoProperties {
    private String base;  // Базовый URL для взаимодействия с Arduino
    // Метод для получения URL для получения данных о свете
    public String getLight() {
        return base + "/light";
    }

}