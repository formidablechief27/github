package com.example.prep.repository;

import com.example.prep.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Questions, Integer> {
	
}
