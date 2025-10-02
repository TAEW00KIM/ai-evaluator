package com.deeplearningbasic.autograder.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private String status;
    private T data;
    private String message;

    // 성공 응답을 만드는 정적 메서드
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("SUCCESS", data, message);
    }

    // 에러 응답을 만드는 정적 메서드
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", null, message);
    }
}