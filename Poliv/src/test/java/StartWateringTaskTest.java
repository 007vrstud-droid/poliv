import static org.mockito.Mockito.*;

import com.example.dto.WateringScheduleItem;
import com.example.properties.WateringProperties;
import com.example.scheduler.StartWateringTask;
import com.example.scheduler.WateringService;
import com.example.scheduler.service.PrognozService;
import com.example.scheduler.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class StartWateringTaskTest {

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private WateringService wateringService;

    @Mock
    private PrognozService prognozService;

    @Mock
    private WateringProperties wateringProperties;

    @InjectMocks
    private StartWateringTask startWateringTask;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartWateringTask_noRain() {
        when(prognozService.shouldSkipWateringDueToRain()).thenReturn(false);
        when(wateringProperties.scheduleCheckEnabled()).thenReturn(true);
        when(wateringProperties.sensorsCheckEnabled()).thenReturn(true);
        when(scheduleService.fetchTodaySchedule()).thenReturn(List.of(new WateringScheduleItem(1, "v1", 30, "10:00", "10:30")));

        startWateringTask.startWateringTask();

        // Verify that wateringService.waterWithScheduleAndSensors is called
        verify(wateringService, times(1)).waterWithScheduleAndSensors(anyList(), anyInt());
    }

    @Test
    void testStartWateringTask_skipDueToRain() {
        when(prognozService.shouldSkipWateringDueToRain()).thenReturn(true);

        startWateringTask.startWateringTask();

        // Verify that no watering function was called
        verify(wateringService, times(0)).waterWithScheduleAndSensors(anyList(), anyInt());
    }

    @Test
    void testStartWateringTask_emptySchedule() {
        when(prognozService.shouldSkipWateringDueToRain()).thenReturn(false);
        when(wateringProperties.scheduleCheckEnabled()).thenReturn(true);
        when(wateringProperties.sensorsCheckEnabled()).thenReturn(false);
        when(scheduleService.fetchTodaySchedule()).thenReturn(Collections.emptyList());

        startWateringTask.startWateringTask();

        // Verify that watering service was not called when schedule is empty
        verify(wateringService, times(0)).waterWithScheduleAndSensors(anyList(), anyInt());
    }
}
