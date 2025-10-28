package com.deeplearningbasic.autograder.projection;

import java.time.LocalDateTime;

public interface LeaderboardRow {
    Long getStudentId();
    String getStudentName();
    Double getBestScore();
    LocalDateTime getLastSubmittedAt();
}