package com.deeplearningbasic.autograder.repository;

import com.deeplearningbasic.autograder.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByStudentIdOrderBySubmissionTimeDesc(Long studentId);
}
