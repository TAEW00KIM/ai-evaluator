package com.deeplearningbasic.autograder.service;

import com.deeplearningbasic.autograder.domain.Submission;
import com.deeplearningbasic.autograder.domain.User;
import com.deeplearningbasic.autograder.dto.AdminSubmissionDto;
import com.deeplearningbasic.autograder.dto.SubmissionRequestDto;
import com.deeplearningbasic.autograder.repository.SubmissionRepository;
import com.deeplearningbasic.autograder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    private final UserRepository userRepository;

    @Transactional
    public Long createSubmission(SubmissionRequestDto requestDto) throws IOException {
        // 1. 파일 저장
        String originalFileName = requestDto.getFile().getOriginalFilename();
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
        Files.copy(requestDto.getFile().getInputStream(), targetLocation);

        // 2. Submission 엔티티 생성 및 DB 저장
        Submission submission = Submission.builder()
                .studentId(requestDto.getStudentId())
                .assignmentId(requestDto.getAssignmentId())
                .filePath(targetLocation.toString())
                .build();

        Submission savedSubmission = submissionRepository.save(submission);

        // 3. TODO: 비동기로 Python 채점 서버 API 호출
        //    (여기서는 일단 저장만 하고 ID를 반환)
        //    callEvaluationServer(savedSubmission.getId(), targetLocation.toString());

        return savedSubmission.getId();
    }

    public Submission findById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid submission ID: " + submissionId));
    }

    public List<AdminSubmissionDto> findAllSubmissionsForAdmin() {
        return submissionRepository.findAll().stream()
                .map(submission -> {
                    // 각 제출(submission)에 해당하는 사용자(user) 정보를 찾음
                    User user = userRepository.findById(submission.getStudentId())
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + submission.getStudentId()));
                    return new AdminSubmissionDto(submission, user);
                })
                .collect(Collectors.toList());
    }
}
