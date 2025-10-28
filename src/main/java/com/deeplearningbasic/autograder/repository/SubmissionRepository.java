package com.deeplearningbasic.autograder.repository;

import com.deeplearningbasic.autograder.domain.Submission;
import com.deeplearningbasic.autograder.projection.LeaderboardRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByStudentIdOrderBySubmissionTimeDesc(Long studentId);

    @Query(value = """
        SELECT
        s.student_id                           AS studentId,
        st.name                                AS studentName,         -
        MAX(s.score)                           AS bestScore,
        MAX(s.submission_time)                 AS lastSubmittedAt
        FROM submission s
        JOIN student st ON st.id = s.student_id  
        WHERE s.assignment_id = :assignmentId
        AND s.status = 'COMPLETED'
        GROUP BY s.student_id, st.name
        ORDER BY bestScore DESC, lastSubmittedAt ASC
        """, nativeQuery = true)
    List<LeaderboardRow> findLeaderboardByAssignment(@Param("assignmentId") Long assignmentId);
}
