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

<<<<<<< HEAD
    public KakaoService(JwtUtil jwtUtil, UserMapper userMapper) {
=======
    private final RestTemplate restTemplate = new RestTemplate();
    public KakaoService(UserMapper userMapper) {
>>>>>>> rebase-branch
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }
    public KakaoUserInfo getKakaoUserInfo(String accessToken){
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

<<<<<<< HEAD
        return  new KakaoUserInfo(nickname, email);
=======
        return new KakaoUserInfo(
                id, connectedAt, nickname, email, hasEmail, isEmailValid, isEmailVerified, hasAgeRange, hasBirthday, hasGender
        );
>>>>>>> rebase-branch
    }

    @Transactional
<<<<<<< HEAD
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
=======
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
            userMapper.updateUserSocialType(existingUser);
            return existingUser;
        }


>>>>>>> rebase-branch
        User user = User.builder()
                .userId("kakao_" + (kakaoUserInfo.getId() != null ? kakaoUserInfo.getId() : email))
                .password(accessToken)
                .hashedPassword(PasswordUtil.sha256(accessToken))
<<<<<<< HEAD
                .email(kakaoUserInfo.getEmail())
                .phone("111-111-111")
                .username(kakaoUserInfo.getNickname())
=======
                .email(email)
                .username(kakaoUserInfo.getNickname() != null ? kakaoUserInfo.getNickname() : "카카오 사용자")
>>>>>>> rebase-branch
                .role("ROLE_USER")
                .verifyYn("Y") // 카카오는 이미 인증이 완료됐으니까 'Y'를 설정해
                .socialType("K") // 카카오의 소셜 타입은 'K'
                .createdAt(LocalDateTime.now())
                .build();

<<<<<<< HEAD
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
=======

        userMapper.insertKakaoUser(user);
        return user;
>>>>>>> rebase-branch
    }


    // 🟢 카카오 API로 userSqno 가져오기
    public String getUserSqnoFromKakao(String kakaoAccessToken) {
        // 1️⃣ 요청 URL 설정 (카카오 사용자 정보 API)
        String url = "https://kapi.kakao.com/v2/user/me";

        // 2️⃣ HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // 3️⃣ API 요청 실행
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("카카오 로그인 API 호출 중 오류 발생: " + e.getMessage());
        }

        // 4️⃣ 응답 데이터 추출
        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody == null || !responseBody.containsKey("id")) {
            throw new RuntimeException("카카오 응답에서 사용자 정보를 찾을 수 없습니다.");
        }

        // 5️⃣ 카카오에서 제공하는 `id` 값을 `userSqno`로 사용
        return responseBody.get("id").toString();
    }

}
