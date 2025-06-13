package com.example.arduino;

import com.example.dto.WateringScheduleItem;

import java.util.List;

public interface ArduinoService {
    void openValve(List<WateringScheduleItem> schedule);
    void closeValve(List<WateringScheduleItem> schedule);
}