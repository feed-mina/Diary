package com.domain.demo_backend.service;

import com.domain.demo_backend.controller.KakaoController;
import com.domain.demo_backend.mapper.UserMapper;
import com.domain.demo_backend.user.domain.User;
import com.domain.demo_backend.user.dto.KakaoUserInfo;
import com.domain.demo_backend.util.PasswordUtil;
import com.domain.demo_backend.util.utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
@Service
public class KakaoService {
    private final UserMapper userMapper;
    private final Logger log = LoggerFactory.getLogger(KakaoService.class);

    public KakaoService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    public KakaoUserInfo getKakaoUserInfo(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<HttpHeaders> request = new HttpEntity<>(headers);

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("getKakaoUserInfo");
        log.info("accessToken : " + accessToken);
        log.info("request : " + request);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                Map.class
        );

        log.info("response : " + response);
        Map<String, Object> body = response.getBody();

        if (body == null) {
            throw new RuntimeException("카카오에서 사용자 정보를 받지 못했어요!");
        }

        log.info("body : " + body);
        log.info("response : " + response);


        // 카카오 계정 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) body.get("properties");

        Long id = ((Number) body.get("id")).longValue();
        String connectedAt = (String) body.get("connected_at");
        String nickname = (String) properties.get("nickname");
        String email = (String) kakaoAccount.get("email");
        boolean hasEmail = (Boolean) kakaoAccount.getOrDefault("has_email", false);
        boolean isEmailValid = (Boolean) kakaoAccount.getOrDefault("is_email_valid", false);
        boolean isEmailVerified = (Boolean) kakaoAccount.getOrDefault("is_email_verified", false);
        boolean hasAgeRange = (Boolean) kakaoAccount.getOrDefault("has_age_range", false);
        boolean hasBirthday = (Boolean) kakaoAccount.getOrDefault("has_birthday", false);
        boolean hasGender = (Boolean) kakaoAccount.getOrDefault("has_gender", false);

        log.info("nickname : " + nickname);
        log.info("email : " + email);

        return new KakaoUserInfo(
                id, connectedAt, nickname, email, hasEmail, isEmailValid, isEmailVerified, hasAgeRange, hasBirthday, hasGender
        );
    }


    @Transactional
    public User registerKakaoUser(KakaoUserInfo kakaoUserInfo, String accessToken){
//        이 로직대로라면 일반회원가입한 사람은 소셜 회원가입할때 이메일이 같으면 회원가입이 어려운데 이럴때 어떻게 일반회원가입한 사람이 카카오로 로그인도 가능하게 할까?
        String email = kakaoUserInfo.getEmail();

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("카카오 계정에 이메일 정보가 없습니다.");
        }

        User existingUser = userMapper.findByUserEmail(kakaoUserInfo.getEmail());

        if (existingUser != null) {
            // 기존 회원이 존재하면 소셜 정보를 업데이트 (예: 일반 가입한 유저가 카카오 로그인 추가)
            existingUser.setSocialType("K");
            existingUser.setVerifyYn("Y");
            userMapper.updateUser(existingUser);
            return existingUser;
        }


        User user = User.builder()
                .userId("kakao_" + (kakaoUserInfo.getId() != null ? kakaoUserInfo.getId() : email))
                .password(accessToken)
                .hashedPassword(PasswordUtil.sha256(accessToken))
                .email(email)
                .username(kakaoUserInfo.getNickname() != null ? kakaoUserInfo.getNickname() : "카카오 사용자")
                .role("ROLE_USER")
                .verifyYn("Y") // 카카오는 인증 완료니까 Y!
                .socialType("K") // 소셜 타입은 K!
                .createdAt(LocalDateTime.now())
                .build();


        userMapper.insertKakaoUser(user);
        return user;
    }



}
