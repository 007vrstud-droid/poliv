package com.example.sensors;

import java.util.List;
import java.util.Map;

public interface MoistureSensorService {
    Map<String, Boolean> checkSoilDryness(List<String> valveIds);
    List<String> getDryValves(List<Integer> valveNumbers);
    List<Map<String, Object>> fetchDrySensors(String url);
}