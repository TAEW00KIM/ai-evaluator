package com.deeplearningbasic.autograder.controller;

import com.deeplearningbasic.autograder.dto.SubmissionRequestDto;
import com.deeplearningbasic.autograder.dto.SubmissionResponseDto;
import com.deeplearningbasic.autograder.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Submissions", description = "과제 제출 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Operation(summary = "과제 제출", description = "학생이 과제 코드를 zip 파일로 제출합니다.")
    @PostMapping
    public ResponseEntity<Long> submitAssignment(
            @Parameter(description = "제출 요청 DTO (파일 포함)")
            @ModelAttribute SubmissionRequestDto requestDto) throws IOException {
        Long submissionId = submissionService.createSubmission(requestDto);
        return ResponseEntity.ok(submissionId);
    }

    @Operation(summary = "제출 결과 조회", description = "제출 ID로 채점 상태 및 결과를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponseDto> getSubmissionResult(
            @Parameter(description = "제출 ID", required = true, example = "1")
            @PathVariable Long id) {
        SubmissionResponseDto responseDto = new SubmissionResponseDto(submissionService.findById(id));
        return ResponseEntity.ok(responseDto);
    }
}