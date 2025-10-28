package com.deeplearningbasic.autograder.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LeaderboardRowDto {
    private Long studentId;
    private String studentName;
    private Double bestScore;
    private String lastSubmittedAt; // ISO 문자열로 내려줄거면 String, 아니면 LocalDateTime

    private Integer rank; // 서비스에서 계산해 세팅
}