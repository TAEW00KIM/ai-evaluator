package com.deeplearningbasic.autograder.dto;

import com.deeplearningbasic.autograder.domain.Submission;
import com.deeplearningbasic.autograder.domain.User; // User 임포트
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminSubmissionDto {
    // Submission 정보
    private final Long submissionId;
    private final LocalDateTime submissionTime;
    private final String status;
    private final Double score;

    // User 정보
    private final Long userId;
    private final String studentName;
    private final String studentEmail;

    // TODO: 추후 Assignment 정보도 추가 가능
    // private final Long assignmentId;
    // private final String assignmentName;

    public AdminSubmissionDto(Submission submission, User user) {
        this.submissionId = submission.getId();
        this.submissionTime = submission.getSubmissionTime();
        this.status = submission.getStatus().name();
        this.score = submission.getScore();

        this.userId = user.getId();
        this.studentName = user.getName();
        this.studentEmail = user.getEmail();
    }
}