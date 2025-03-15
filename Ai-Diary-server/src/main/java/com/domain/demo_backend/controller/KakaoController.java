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
        return ResponseEntity.ok(response);
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

    @GetMapping("/pomoklogin")
    public String pomoklogin() {
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("kakao login");
        log.info("client_id : " + clientId);
        log.info("redirectUri : " + redirectUri);

        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
    }
    @PostMapping("/sendRecord")
    public ResponseEntity<String> sendRecord(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestBody Map<String, Object> data) {

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
