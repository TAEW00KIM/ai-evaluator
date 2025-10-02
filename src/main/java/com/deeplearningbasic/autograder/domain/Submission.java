package com.deeplearningbasic.autograder.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long assignmentId;

    @Column(nullable = false)
    private String filePath;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime submissionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    private Double score;

    @Lob
    private String log;

    @Builder
    public Submission(Long studentId, Long assignmentId, String filePath) {
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.filePath = filePath;
        this.status = SubmissionStatus.PENDING;
    }

    public void complete(Double score, String log) {
        this.status = SubmissionStatus.COMPLETED;
        this.score = score;
        this.log = log;
    }

    public void error(String log) {
        this.status = SubmissionStatus.ERROR;
        this.log = log;
    }
}
