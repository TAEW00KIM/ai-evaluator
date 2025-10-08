package com.deeplearningbasic.autograder.repository;

import com.deeplearningbasic.autograder.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}