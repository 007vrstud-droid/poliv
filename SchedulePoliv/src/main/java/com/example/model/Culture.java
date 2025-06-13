package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cultures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Culture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays;
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    @Column(name = "last_watered")
    private LocalDate lastWatered;
}