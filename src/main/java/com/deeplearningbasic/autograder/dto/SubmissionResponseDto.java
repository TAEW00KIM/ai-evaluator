package com.deeplearningbasic.autograder.dto;

import com.deeplearningbasic.autograder.domain.Submission;
import com.deeplearningbasic.autograder.domain.SubmissionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class SubmissionResponseDto {
    private final Long id;
    private final Long studentId;
    private final Long assignmentId;
    private final LocalDateTime submissionTime;
    private final SubmissionStatus status;
    private final Double score;
    private final String log;

    public SubmissionResponseDto(Submission submission) {
        this.id = submission.getId();
        this.studentId = submission.getStudentId();
        this.assignmentId = submission.getAssignmentId();
        this.submissionTime = submission.getSubmissionTime();
        this.status = submission.getStatus();
        this.score = submission.getScore();
        this.log = submission.getLog();
    }
}
