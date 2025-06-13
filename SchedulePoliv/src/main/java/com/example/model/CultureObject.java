package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "culture_objects")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CultureObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "sensor_id", nullable = false)
    private Integer sensorId;
    @Column(name = "valve_id", nullable = false)
    private String valveId;
    @Column(nullable = false)
    private String zone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "culture_id", nullable = false)
    private Culture culture;
}