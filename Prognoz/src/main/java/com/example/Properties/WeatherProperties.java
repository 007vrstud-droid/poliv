package com.example.Properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "myapp.services.weather")
@Data
public class WeatherProperties {
    private String url;
    private String apiKey;
    private double rainThreshold;
}
