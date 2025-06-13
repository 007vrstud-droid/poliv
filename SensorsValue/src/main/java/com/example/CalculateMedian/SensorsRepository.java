package com.example.CalculateMedian;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SensorsRepository extends JpaRepository<Sensors, Integer> {
 Optional<Sensors> findByName(String name);
}