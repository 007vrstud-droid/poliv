package com.example.CalculateMedian;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_sensors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SensorsData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensors sensor;
    @Column(name = "sensor_value",nullable = false)
    private BigDecimal sensorValue;
    @CreationTimestamp
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
