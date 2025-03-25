package com.domain.demo_backend.controller;


import com.domain.demo_backend.service.AuthService;
import com.domain.demo_backend.service.KakaoService;
import com.domain.demo_backend.user.domain.User;
import com.domain.demo_backend.user.dto.KakaoAuthRequest;
import com.domain.demo_backend.user.dto.KakaoAuthResponse;
import com.domain.demo_backend.user.dto.KakaoUserInfo;
import com.domain.demo_backend.user.dto.RegisterRequest;
import com.domain.demo_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@RestController
@RequestMapping("/api/kakao")
public class KakaoController {
    private final Logger log = LoggerFactory.getLogger(KakaoController.class);
    // application.properties 에 있는 값 불러오기

    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    // 생성자 주입
    @Autowired
    public KakaoController(KakaoService kakaoService, JwtUtil jwtUtil) {
        this.kakaoService = kakaoService;
        this.jwtUtil = jwtUtil;
    }

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    private String accessToken;

    private static final String KAKAO_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoAuthRequest kakaoAuthRequest) {

        try {
        // 로그로 디버그 정보 출력
        log.info("kakao login");
        log.info("client_id : " + clientId);
        log.info("redirectUri : " + redirectUri);

        // 1. 받은 AccessToken으로 카카오에서 사용자 정보를 가져와
        KakaoUserInfo kakaoUserInfo = kakaoService.getKakaoUserInfo(kakaoAuthRequest.getAccessToken());

        // 2. 사용자 정보를 이용해 DB에 회원가입 또는 조회를 진행해
        // JWT 토큰을 발급받아
        String jwtToken = kakaoService.registerKakaoUser(kakaoUserInfo, kakaoAuthRequest.getAccessToken());

        // 3. JWT 토큰을 클라이언트에 응답으로 보내줘
        KakaoAuthResponse response = new KakaoAuthResponse(kakaoUserInfo, jwtToken);
            return ResponseEntity.ok(response); // Vue에서 kakaoUserInfo.email 받기 위해 필요
        } catch (Exception e) {
            log.error("❌ 카카오 로그인 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("카카오 로그인 실패");
        }
    }

    @GetMapping("/callback")
    public String getAccessToken(@RequestParam String code) {

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("kakao callback");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&code=" + code;

        log.info("headers : " + headers);
        log.info("body : " + body);
        log.info("client_id : " + clientId);
        log.info("redirectUri : " + redirectUri);
        log.info("code : " + code);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                Map.class
        );

        accessToken = (String) response.getBody().get("access_token");
        log.info("accessToken : " + accessToken);

        return "Access Token 발급 성공! : " + accessToken;
    }

    @PostMapping("/sendRecord")
    public ResponseEntity<String> sendRecord(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> data) {

        log.info("📢 Received Authorization header: {}", authorization);

        // ✅ Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("❌ Authorization 헤더가 없거나 잘못됨: {}", authorization);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("카카오 토큰이 필요합니다.");
        }

        // ✅ JWT 검증
          String jwtToken = authorization.substring(7);
        log.info("✅ Extracted Access Token: {}", jwtToken);

        if (jwtToken.isEmpty()) {
            log.error("❌ 추출한 Access Token이 비어 있음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰");
        }

        // ✅ 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo;
        try {
            kakaoUserInfo = kakaoService.getKakaoUserInfo(accessToken);
        } catch (Exception e) {
            log.error("❌ 카카오 사용자 정보 조회 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("카카오 사용자 정보 조회 실패");
        }

        // ✅ JWT 토큰 발급
        String Token = kakaoService.registerKakaoUser(kakaoUserInfo, accessToken);
        log.info("✅ 발급된 Token: {}", Token);

        // ✅ 클라이언트 로그인 유도 (필요 시)
        if (clientId == null || redirectUri == null) {
            String loginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                    + clientId + "&redirect_uri=" + redirectUri;
            log.info("🔄 로그인이 필요해요. 로그인 페이지로 이동!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginUrl);
        }

        // ✅ 전송할 데이터 정리
        Integer stopwatchTime = (Integer) data.getOrDefault("stopwatchTime", 0);
        Integer pomodoroCount = (Integer) data.getOrDefault("pomodoroCount", 0);
        Integer pomodoroTotalTime = (Integer) data.getOrDefault("pomodoroTotalTime", 0);

        log.info("📌 stopwatchTime: {}초, pomodoroCount: {}회, pomodoroTotalTime: {}분",
                stopwatchTime, pomodoroCount, pomodoroTotalTime);

        // ✅ 메시지 구성
        StringBuilder message = new StringBuilder();

        if (stopwatchTime > 0) {
            int minutes = stopwatchTime / 60;
            int seconds = stopwatchTime % 60;
            message.append("⏱️ 스탑워치 기록: ").append(minutes).append("분 ").append(seconds).append("초\n");
        }

        if (pomodoroCount > 0 && pomodoroTotalTime > 0) {
            message.append("🍅 뽀모도로: ").append(pomodoroCount)
                    .append("회, 총 ").append(pomodoroTotalTime).append("분 완료!");
        }

        if (message.length() == 0) {
            message.append("❗ 기록이 없어요.");
        }

        String messageText = message.toString().replace("\n", "\\n").replace("\"", "\\\"");
        log.info("📩 최종 메시지: {}", messageText);

        // ✅ 카카오톡 메시지 전송 준비
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", "{"
                + "\"object_type\":\"text\","
                + "\"text\":\"" + messageText + "\","
                + "\"link\":{\"web_url\":\"https://www.kakao.com\"}"
                + "}");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        log.info("📤 카카오 API 요청: {}", request);

        // ✅ 카카오 API 요청 전송
        try {
            ResponseEntity<String> response = new RestTemplate().postForEntity(KAKAO_URL, request, String.class);
            log.info("✅ 카카오톡 메시지 전송 성공! 응답: {}", response);
            return ResponseEntity.ok("카톡 전송 성공!");
        } catch (HttpClientErrorException e) {
            log.error("❌ 카톡 전송 실패! 오류: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("카톡 전송 실패! 오류: " + e.getResponseBodyAsString());
        }
    }


}