package com.example.repository;

import com.example.model.Culture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CultureRepository extends JpaRepository<Culture, Long> {
}
