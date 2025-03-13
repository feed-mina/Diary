package com.domain.demo_backend.controller;


import com.domain.demo_backend.service.AuthService;
import com.domain.demo_backend.service.KakaoService;
import com.domain.demo_backend.user.domain.User;
import com.domain.demo_backend.user.dto.KakaoAuthRequest;
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
    private final AuthService authService;
    private final JwtUtil jwtUtil;


    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;

    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    private String accessToken;

    private static final String KAKAO_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";


    @Autowired
    public KakaoController(KakaoService kakaoService, AuthService authService, JwtUtil jwtUtil) {
        this.kakaoService = kakaoService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> requestBody) {
        log.info("🔹 카카오 로그인 시작");

        String kakaoAccessToken = requestBody.get("token");
        System.out.println("📌 카카오에서 받은 access_token: " + kakaoAccessToken);

        if (kakaoAccessToken == null || kakaoAccessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid Kakao Token"));
        }

        // 🔹 카카오 토큰 검증
        String kakaoResponse = validateKakaoToken(kakaoAccessToken);
        if (kakaoResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Kakao Token"));
        }

        // 🔹 사용자 정보 가져오기
        KakaoUserInfo kakaoUserInfo = kakaoService.getKakaoUserInfo(kakaoAccessToken);
        if (kakaoUserInfo == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to get user info"));
        }

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("kakao login");
        log.info("client_id : " + clientId);
        log.info("redirectUri : " + redirectUri);

        // 🔹 사용자 등록 또는 조회
        User user = kakaoService.registerKakaoUser(kakaoUserInfo, kakaoAccessToken);

        // 🔹 JWT 발급
        String jwtToken = jwtUtil.createToken(user.getUsername(), user.getUserSqno(), user.getUserId());
        return ResponseEntity.ok(Map.of("jwtToken", jwtToken));
    }

    // ✅ 카카오 토큰 유효성 검사
    public String validateKakaoToken(String kakaoAccessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + kakaoAccessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v1/user/access_token_info",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("✅ 카카오 토큰 검증 성공: " + response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("❌ 카카오 토큰 검증 실패: " + e.getMessage());
            return null;
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
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestBody Map<String, Object> data)
    {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("카카오 토큰이 필요");
        }
        String accessToken = authorization.substring(7); // "Bearer " 이후 토큰 부분만 추출

        log.info("@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("kakao sendRecord");
        log.info("kakao sendRecord data:" + data);
        log.info("kakao sendRecord accessToken:" + accessToken);
        // clientId, redirectUri 체크
        if (clientId == null || redirectUri == null) {
            String loginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
            log.info("로그인이 필요해요. 로그인 페이지로 보내줄게요!");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401번 오류
                    .body(loginUrl); // 로그인 주소 보내기
        }

        if (accessToken == null) {
            log.info("먼저 카카오 로그인해서 Access Token을 받아야 해요");
            String loginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(loginUrl);
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // Access Token 넣기
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Integer stopwatchTime = data.get("stopwatchTime") != null ? (Integer) data.get("stopwatchTime") : 0;
        Integer pomodoroCount = data.get("pomodoroCount") != null ? (Integer) data.get("pomodoroCount") : 0;
        Integer pomodoroTotalTime = data.get("pomodoroTotalTime") != null ? (Integer) data.get("pomodoroTotalTime") : 0;

        StringBuilder message = new StringBuilder();

        log.info("stopwatchTime : " + stopwatchTime);
        log.info("pomodoroCount : " + pomodoroCount);
        log.info("pomodoroTotalTime : " + pomodoroTotalTime);
        // 스탑워치 처리 로직
        if (stopwatchTime != null && stopwatchTime > 0) {
            int minutes = stopwatchTime / 60;
            int seconds = stopwatchTime % 60;
            message.append("⏱️ 스탑워치 기록: " + minutes + "분 " + seconds + "초\n");
        }

        if (pomodoroCount != null && pomodoroCount > 0 && pomodoroTotalTime != null && pomodoroTotalTime > 0) {
            message.append("🍅 뽀모도로: " + pomodoroCount + "회, 총 " + pomodoroTotalTime + "분 완료!");
        }

        if (message.length() == 0) {
            message.append("❗ 기록이 없어요.");
        }

        // 메시지가 정상적으로 만들어졌는지 확인
        String messageText = message.toString();

        if (messageText.isEmpty() || messageText.equals("-")) {
            messageText = "❗ 기록이 없어요.";
        }
        messageText = messageText.replace("\n", "\\n").replace("\"", "\\\"");

        // 카톡에 보낼 메시지 내용
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("template_object", "{"
//                + "\"object_type\":\"text\","
//                + "\"text\":\"" + messageText.replace("\"", "\\\"") + "\","
//                + "\"link\":{\"web_url\":\"https://www.kakao.com\"}"
//                + "}");
        params.add("template_object", "{"
                + "\"object_type\":\"text\","
                + "\"text\":\"" + messageText + "\","
                + "\"link\":{\"web_url\":\"https://www.kakao.com\"}"
                + "}");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        log.info("request : " + request);


        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    KAKAO_URL,
                    request,
                    String.class
            );
            log.info("response : " + response);

            return ResponseEntity.ok("카톡 전송 성공!");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("카톡 전송 실패! 오류: " + e.getResponseBodyAsString());
        }


    }

}
