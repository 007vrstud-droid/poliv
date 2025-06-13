package com.example.settings;


import com.example.properties.WateringProperties;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/watering/manual")
public class ManualWateringController {

    private final WateringSettingsService service;

    public ManualWateringController(WateringSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public WateringProperties.ManualWatering getManualSettings() {
        return service.getSettings().manualWatering();
    }

    @PostMapping
    public void updateManual(@RequestBody ManualWateringDto dto) {
        service.updateManualSettings(dto.enabled(), dto.time(), dto.durationMinutes(), dto.valves());
    }
}