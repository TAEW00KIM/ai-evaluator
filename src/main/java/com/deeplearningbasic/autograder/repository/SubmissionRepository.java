package com.deeplearningbasic.autograder.repository;

import com.deeplearningbasic.autograder.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
