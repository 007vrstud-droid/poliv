package com.example.settings;


import com.example.properties.WateringProperties;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/watering/auto")
public class AutoWateringController {

    private final WateringSettingsService service;

    public AutoWateringController(WateringSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public WateringProperties getAutoSettings() {
        return service.getSettings();
    }

    @PostMapping
    public void updateAuto(@RequestBody AutoWateringDto dto) {
        service.updateAutoSettings(
                dto.enabled(),
                dto.scheduleCheckEnabled(),
                dto.prognozCheckEnabled(),
                dto.sensorsCheckEnabled(),
                dto.sensorsCheckIntervalSeconds(),
                dto.scheduleCron()
        );
    }
}