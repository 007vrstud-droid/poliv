package com.example.CalculateMedian;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sensors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Автоматическое генерирование ID
    private Integer id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private String zone;
    @Column(name = "controller_id", nullable = false)
    private Integer controllerId;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SensorsData> sensorsData;
    @Column(name = "humidity")
    private Integer humidity;
}