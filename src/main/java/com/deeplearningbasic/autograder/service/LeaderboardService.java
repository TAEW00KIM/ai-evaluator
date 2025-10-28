package com.deeplearningbasic.autograder.service;

import com.deeplearningbasic.autograder.dto.LeaderboardRowDto;
import com.deeplearningbasic.autograder.projection.LeaderboardRow;
import com.deeplearningbasic.autograder.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardService {

    private final SubmissionRepository submissionRepository;

    public List<LeaderboardRowDto> getLeaderboard(Long assignmentId) {
        List<LeaderboardRow> rows = submissionRepository.findLeaderboardByAssignment(assignmentId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<LeaderboardRowDto> list = rows.stream().map(r -> {
            Long studentId = r.getStudentId();
            String studentName = r.getStudentName();
            Double bestScore = r.getBestScore() == null ? 0.0 : r.getBestScore();

            String lastSubmittedAtStr = null;

            LocalDateTime ldt = r.getLastSubmittedAt();
            if (ldt != null) {
                lastSubmittedAtStr = ldt.format(fmt);
            }
            return LeaderboardRowDto.builder()
                    .studentId(studentId)
                    .studentName(studentName)
                    .bestScore(bestScore)
                    .lastSubmittedAt(lastSubmittedAtStr)
                    .build();
        }).toList();

        // Dense rank
        int rank = 0;
        Double prev = null;
        for (LeaderboardRowDto row : list) {
            if (prev == null || !prev.equals(row.getBestScore())) {
                rank++;
                prev = row.getBestScore();
            }
            row.setRank(rank);
        }
        return list;
    }
}
