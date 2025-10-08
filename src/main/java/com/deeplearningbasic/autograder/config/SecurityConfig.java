package com.deeplearningbasic.autograder.config;

import com.deeplearningbasic.autograder.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // 1. 인증 없이 접근을 허용할 경로들
                        .requestMatchers(
                                "/",
                                "/login/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/internal/**"
                        ).permitAll()
                        // 2. ADMIN 권한이 있어야만 접근 가능한 경로 (더 구체적인 경로를 먼저!)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 3. USER 또는 ADMIN 권한이 있으면 접근 가능한 경로
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        // 4. 나머지 모든 요청은 인증만 되면 접근 가능
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout") // POST 요청을 처리할 로그아웃 경로
                        .logoutSuccessUrl("http://localhost:5173/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:5173", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        return http.build();
    }

//    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
//        return (request, response, authentication) -> {
//            // Spring Security가 세션을 무효화한 후, Google 로그아웃 URL로 리디렉션합니다.
//            String googleLogoutUrl = "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=http://localhost:5173/login";
//            response.sendRedirect(googleLogoutUrl);
//        };
//    }
}

