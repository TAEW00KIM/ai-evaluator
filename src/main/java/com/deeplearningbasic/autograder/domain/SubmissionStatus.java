package com.deeplearningbasic.autograder.domain;

import jakarta.persistence.Enumerated;

public enum SubmissionStatus {
    PENDING, RUNNING, COMPLETED, ERROR
}
