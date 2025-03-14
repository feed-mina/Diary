package com.domain.demo_backend.service;

import com.domain.demo_backend.controller.KakaoController;
import com.domain.demo_backend.mapper.UserMapper;
import com.domain.demo_backend.user.domain.User;
import com.domain.demo_backend.user.dto.KakaoUserInfo;
import com.domain.demo_backend.util.JwtUtil;
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
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final Logger log = LoggerFactory.getLogger(KakaoService.class);

    public KakaoService(JwtUtil jwtUtil, UserMapper userMapper) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
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

        return  new KakaoUserInfo(nickname, email);
    }

    @Transactional
    public String  registerKakaoUser(KakaoUserInfo kakaoUserInfo, String accessToken){
        // DB에서 같은 이메일이 있는지 확인해
        if(userMapper.findByUserEmail(kakaoUserInfo.getEmail()) != null){

            // 이미 존재하는 경우 updated_at 갱신
            userMapper.updateUpdatedAt(kakaoUserInfo.getEmail());
            System.out.println("@@@@@@@@@userMapper.findByUSerEmail"+userMapper.findByUserEmail(kakaoUserInfo.getEmail()));

        String Auth = String.valueOf(userMapper.findByUserEmail(kakaoUserInfo.getEmail()));
            System.out.println("@@@@@@@@@userMapper.findByUSerEmail"+Auth);
            return "c41fbbc45272e69d15fab75908c2ac32e18d3dc65dc9d69186e4507e7b7e37b5";
        }

        // 새로운 사용자 객체 생성 (빌더 패턴 사용)
        User user = User.builder()
                .userId("kakao_" + kakaoUserInfo.getEmail())
                .password(accessToken)
                .hashedPassword(PasswordUtil.sha256(accessToken))
                .email(kakaoUserInfo.getEmail())
                .phone("111-111-111")
                .username(kakaoUserInfo.getNickname())
                .role("ROLE_USER")
                .verifyYn("Y") // 카카오는 이미 인증이 완료됐으니까 'Y'를 설정해
                .socialType("K") // 카카오의 소셜 타입은 'K'
                .createdAt(LocalDateTime.now())
                .build();

        // 사용자 정보를 DB에 저장해
        userMapper.insertUser(user);

        // DB에 저장 후 자동 생성된 사용자 고유 번호(userSqno)가 있으면
        if(user.getUserSqno() != null) {
            // JWT 토큰을 생성해 반환해
            String jwtToken = jwtUtil.createToken(user.getUsername(), user.getUserSqno(), user.getUserId());
            return jwtToken;
        } else {
            throw new RuntimeException("userSqno가 null입니다.");
        }

        // return user; -> 이 코드는 실행되지 않아
    }



}
