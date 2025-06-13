package com.example.repository;

import com.example.model.CultureObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CultureObjectRepository extends JpaRepository<CultureObject, Long> {
    List<CultureObject> findByCultureId(Long cultureId);
}
