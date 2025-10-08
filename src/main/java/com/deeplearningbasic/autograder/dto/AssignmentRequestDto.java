package com.deeplearningbasic.autograder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentRequestDto {
    private String title;
    private String description;
}