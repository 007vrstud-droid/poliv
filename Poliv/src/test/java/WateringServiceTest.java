import static org.mockito.Mockito.*;

import com.example.arduino.ArduinoService;
import com.example.dto.WateringScheduleItem;
import com.example.properties.WateringProperties;
import com.example.scheduler.WateringService;
import com.example.sensors.MoistureSensorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class WateringServiceTest {

    @Mock
    private ArduinoService arduinoService;

    @Mock
    private MoistureSensorService moistureSensorService;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private WateringProperties wateringProperties;

    @InjectMocks
    private WateringService wateringService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Моким TaskScheduler для немедленного выполнения задач
        when(taskScheduler.scheduleAtFixedRate(any(Runnable.class), any(Duration.class)))
                .thenAnswer(invocation -> {
                    Runnable task = invocation.getArgument(0);
                    task.run(); // сразу выполняем Runnable
                    return mock(ScheduledFuture.class);
                });

        when(taskScheduler.schedule(any(Runnable.class), any(Instant.class)))
                .thenAnswer(invocation -> {
                    Runnable task = invocation.getArgument(0);
                    task.run(); // сразу выполняем Runnable
                    return mock(ScheduledFuture.class);
                });
    }

    @Test
    void testWaterWithScheduleAndSensors() {
        when(wateringProperties.enabled()).thenReturn(true);
        when(wateringProperties.sensorsCheckEnabled()).thenReturn(true);

        // мок ответа сенсоров
        when(moistureSensorService.fetchDrySensors(anyString()))
                .thenReturn(List.of(Map.of("name", "sensor1")));

        List<WateringScheduleItem> schedule =
                List.of(new WateringScheduleItem(1, "v1", 30, "10:00", "10:30"));

        wateringService.waterWithScheduleAndSensors(schedule, 30);

        verify(arduinoService, atLeastOnce()).openValve(anyList());
    }

    @Test
    void testWaterWithoutSensors() {
        List<WateringScheduleItem> schedule =
                List.of(new WateringScheduleItem(1, "v1", 30, "10:00", "10:30"));

        wateringService.waterWithoutSensors(schedule);

        verify(arduinoService, atLeastOnce()).openValve(anyList());
        verify(arduinoService, atLeastOnce()).closeValve(anyList());
    }

    @Test
    void testWaterWithSensorsOnlyByDryness() {
        List<String> sensorNames = List.of("sensor1", "sensor2");

        when(moistureSensorService.fetchDrySensors(anyString()))
                .thenReturn(List.of(Map.of("name", "sensor1")));

        wateringService.waterWithSensorsOnlyByDryness(sensorNames, 30);

        verify(arduinoService, atLeastOnce()).openValve(anyList());
    }
}
