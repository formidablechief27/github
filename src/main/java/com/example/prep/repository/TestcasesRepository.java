package com.example.prep.repository;

import com.example.prep.model.Testcases;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestcasesRepository extends JpaRepository<Testcases, Integer> {
    // Custom query methods (if needed) can be defined here
}
