package com.example;

import com.example.properties.WateringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(WateringProperties.class)  // <--- ЭТО ВАЖНО
public class Poliv {
    public static void main(String[] args) {
        SpringApplication.run(Poliv.class, args);
    }
}