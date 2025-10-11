package com.deeplearningbasic.autograder.config;

import com.deeplearningbasic.autograder.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/api-docs",
                "/api-docs/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/webjars/**",
                "/favicon.ico",
                "/error"
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(java.util.List.of(
                "http://203.253.70.211:18081",
                "http://localhost:5173"
        ));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @Order(0)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/swagger-ui.html",
                             "/swagger-ui/**",
                             "/api-docs",
                             "/api-docs/**",
                             "/v3/api-docs",
                             "/v3/api-docs/**",
                             "/webjars/**")
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/", "/login/**",
                                "/api/internal/**",
                                "/actuator/**",
                                "/api/user/me"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessUrl("http://localhost:5173/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler((req, res, auth) -> {
                            // ✅ 로그인 성공 시 무조건 홈("/")으로 리다이렉트
                            res.setStatus(302);
                            res.setHeader("Location", "/");
                        })
                        .failureHandler((req, res, ex) -> {
                            String reason = ex.getMessage();
                            if (reason == null) reason = "oauth2_error";
                            try {
                                String enc = java.net.URLEncoder.encode(reason, java.nio.charset.StandardCharsets.UTF_8);
                                res.setStatus(302);
                                res.setHeader("Location", "/login?error=" + enc);
                            } catch (Exception e) {
                                res.setStatus(302);
                                res.setHeader("Location", "/login?error=oauth2_error");
                            }
                        })
                );

        return http.build();
    }
}