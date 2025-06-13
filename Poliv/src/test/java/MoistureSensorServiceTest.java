import com.example.sensors.MoistureSensorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
public class MoistureSensorServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MoistureSensorServiceImpl moistureSensorService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckSoilDryness() {
        List<String> valveIds = List.of("v1", "v2");

        // Сервис возвращает Map<String, Boolean>
        Map<String, Boolean> dryness = Map.of("v1", true, "v2", false);

        // Мокаем RestTemplate с ParameterizedTypeReference, чтобы убрать неоднозначность
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Boolean>>>any()
        )).thenReturn(new ResponseEntity<>(dryness, HttpStatus.OK));

        Map<String, Boolean> result = moistureSensorService.checkSoilDryness(valveIds);

        assertEquals(dryness, result);
    }

    @Test
    void testFetchDrySensors() {
        String url = "http://localhost:8083/sensor-stats/dry-sensors";
        List<Map<String, Object>> expectedResponse = List.of(Map.of("name", "sensor1"));

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                eq(null),
                ArgumentMatchers.<ParameterizedTypeReference<List<Map<String, Object>>>>any()
        )).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        List<Map<String, Object>> result = moistureSensorService.fetchDrySensors(url);

        assertEquals(expectedResponse, result);
    }
}
