package com.deeplearningbasic.autograder.controller;

import com.deeplearningbasic.autograder.domain.User;
import com.deeplearningbasic.autograder.dto.ApiResponse;
import com.deeplearningbasic.autograder.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;

@Tag(name = "User", description = "사용자 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getMyInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
        // 로그인되지 않은 상태: 200 + data:null 반환 (프론트가 스피너 해제하고 비로그인 UI 렌더)
        if (oAuth2User == null) {
            return ResponseEntity.ok(ApiResponse.success(null, "로그인되지 않은 사용자"));
        }

        // OAuth2 공급자로부터 이메일을 안전하게 추출
        String email = Objects.toString(oAuth2User.getAttribute("email"), null);
        if (email == null) {
            // email 클레임이 없을 수 있으므로(프로바이더/권한 범위에 따라), 안전 처리
            return ResponseEntity.ok(ApiResponse.success(null, "이메일 정보를 확인할 수 없습니다"));
        }

        // DB 조회: 미등록 사용자이면 null 응답으로 처리(필요시 여기서 자동 가입 로직 추가 가능)
        User user = userRepository.findByEmail(email).orElse(null);
        return ResponseEntity.ok(ApiResponse.success(user, user != null ? "내 정보 조회 성공" : "등록된 사용자 없음"));
    }
}