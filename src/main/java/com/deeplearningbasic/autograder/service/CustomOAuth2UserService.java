package com.deeplearningbasic.autograder.service;

import com.deeplearningbasic.autograder.domain.Role;
import com.deeplearningbasic.autograder.domain.User;
import com.deeplearningbasic.autograder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (!email.endsWith("@hufs.ac.kr")) {
             throw new OAuth2AuthenticationException("Invalid email domain");
        }

        User user = saveOrUpdate(email, name);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes,
                "email" // 사용자 이름(primary key)으로 사용할 속성 키
        );
    }

    private User saveOrUpdate(String email, String name) {
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name)) // 기존 사용자는 이름 업데이트
                .orElse(User.builder() // 신규 사용자는 새로 생성
                        .email(email)
                        .name(name)
                        .role(Role.USER) // 기본 권한은 USER
                        .build());
        return userRepository.save(user);
    }
}