package com.deeplearningbasic.autograder.service;

import com.deeplearningbasic.autograder.config.AdminProperties;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AdminProperties adminProperties;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null || !email.contains("@")) {
            throw new OAuth2AuthenticationException("missing_email");
        }
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase(Locale.ROOT);
        // TODO: 필요 시 application-prod.yml 로 빼서 구성 가능. 지금은 임시로 허용 도메인 2개만 둠.
        List<String> allowedDomains = Arrays.asList("hufs.ac.kr", "gmail.com");
        if (!allowedDomains.contains(domain)) {
            throw new OAuth2AuthenticationException("invalid_email_domain:" + domain);
        }

        User user = saveOrUpdate(email, name);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes,
                "email" // 사용자 이름(primary key)으로 사용할 속성 키
        );
    }

    private User saveOrUpdate(String email, String name) {
        // 관리자 이메일 목록에 포함되어 있는지 확인하여 역할(Role) 결정
        Role role = adminProperties.getEmails().contains(email) ? Role.ADMIN : Role.USER;

        User user = userRepository.findByEmail(email)
                // 이미 존재하는 사용자인 경우, 이름과 역할을 업데이트
                .map(entity -> entity.update(name, role))
                // 신규 사용자인 경우, 결정된 역할로 새로 생성
                .orElse(User.builder()
                        .email(email)
                        .name(name)
                        .role(role)
                        .build());
        return userRepository.save(user);
    }

}