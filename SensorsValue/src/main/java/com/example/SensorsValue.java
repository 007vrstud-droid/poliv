package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@ConfigurationProperties(prefix = "myapp")
@EnableScheduling
public class SensorsValue {
    public static void main(String[] args) {
        SpringApplication.run(SensorsValue.class, args);
    }
}