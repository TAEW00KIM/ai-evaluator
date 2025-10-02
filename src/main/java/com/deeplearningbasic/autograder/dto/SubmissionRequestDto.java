package com.deeplearningbasic.autograder.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SubmissionRequestDto {
    private Long studentId;
    private Long assignmentId;
    private MultipartFile file;
}
