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

        Map<String, Object> body = response.getBody();

        if (body == null) {
            throw new RuntimeException("카카오에서 사용자 정보를 받지 못했어요!");
        }

        log.info("body : " + body);
        log.info("response : " + response);

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) body.get("properties");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String , Object>) response.getBody().get("properties")).get("nickname");
        log.info("kakaoAccount : " + kakaoAccount);
        log.info("properties : " + properties);

        log.info("nickname : " + nickname);
        log.info("email : " + email);

        return  new KakaoUserInfo(email, nickname);
    }


    @Transactional
    public User registerKakaoUser(KakaoUserInfo kakaoUserInfo, String accessToken){
//        이 로직대로라면 일반회원가입한 사람은 소셜 회원가입할때 이메일이 같으면 회원가입이 어려운데 이럴때 어떻게 일반회원가입한 사람이 카카오로 로그인도 가능하게 할까?
        if(userMapper.findByUserEmail(kakaoUserInfo.getEmail()) != null){
            throw new utility.DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .userId("kakao_" + kakaoUserInfo.getEmail())
                .password(accessToken)
                .hashedPassword(PasswordUtil.sha256(accessToken))
                .email(kakaoUserInfo.getEmail())
                .username(kakaoUserInfo.getNickname())
                .role("ROLE_USER")
                .verifyYn("Y") // 카카오는 인증 완료니까 Y!
                .socialType("K") // 소셜 타입은 K!
                .createdAt(LocalDateTime.now())
                .build();


        userMapper.insertUser(user);
        return user;
    }



}
