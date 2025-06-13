//package com.example;
//
//import com.example.SaveSensorsValues.ArduinoSensorValue;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//@RestController
//@RequiredArgsConstructor
//public class TestController {
//    private final ArduinoSensorValue arduinoSensorValue;
//    @GetMapping("/api/arduino/light")
//    public ResponseEntity<Map<String, Integer>> getLightData() {
//        return ResponseEntity.ok(arduinoSensorValue.getDatSvetaValue());
//    }
//
//}
