package com.example.prep.repository;

import com.example.prep.model.Mcq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface McqRepository extends JpaRepository<Mcq, Long> {
}
