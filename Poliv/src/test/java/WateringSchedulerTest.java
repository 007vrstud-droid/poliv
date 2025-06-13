import com.example.properties.WateringProperties;
import com.example.scheduler.StartManualWateringTask;
import com.example.scheduler.StartWateringTask;
import com.example.scheduler.WateringScheduler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class WateringSchedulerTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private WateringProperties wateringProperties;

    @Mock
    private StartWateringTask startWateringTask;

    @Mock
    private StartManualWateringTask startManualWateringTask;

    @InjectMocks
    private WateringScheduler wateringScheduler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScheduleTask_manualEnabled() {
        WateringProperties.ManualWatering manual = mock(WateringProperties.ManualWatering.class);

        when(wateringProperties.enabled()).thenReturn(true);
        when(wateringProperties.manualWatering()).thenReturn(manual);
        when(manual.enabled()).thenReturn(true);
        when(manual.time()).thenReturn("10:30");

        wateringScheduler.scheduleTask();

        verify(taskScheduler, times(1))
                .schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void testScheduleTask_scheduleEnabled() {
        when(wateringProperties.enabled()).thenReturn(true);
        when(wateringProperties.scheduleCheckEnabled()).thenReturn(true);
        when(wateringProperties.scheduleCron()).thenReturn("0 30 10 * * *");

        wateringScheduler.scheduleTask();

        // Verify that schedule task was scheduled
        verify(taskScheduler, times(1)).schedule(any(Runnable.class), any(CronTrigger.class));
    }

    @Test
    void testScheduleTask_allDisabled() {
        when(wateringProperties.enabled()).thenReturn(false);

        wateringScheduler.scheduleTask();

        // Verify that no tasks were scheduled
        verify(taskScheduler, times(0)).schedule(any(Runnable.class), any(CronTrigger.class));
    }
}
