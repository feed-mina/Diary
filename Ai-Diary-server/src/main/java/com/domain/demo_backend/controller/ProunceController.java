package com.domain.demo_backend.controller;

import com.domain.demo_backend.util.WordResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import scala.collection.Seq;
import scala.collection.JavaConverters;

@RestController
@RequestMapping("/api")
public class PronounceController {

    @PostMapping("/check")
    public ResponseEntity<?> check(@RequestBody WordRequest request) {
        String word1 = request.getWord1();
        String word2 = request.getWord2();

        // 발음 변환 (예시로 소문자로만 처리)
        String pron1 = word1.toLowerCase();
        String pron2 = word2.toLowerCase();

        // 라벤슈타인 거리 계산
        int distance = getLevenshteinDistance(pron1, pron2);

        String message = "두 단어의 발음 차이는 " + distance + " 입니다.";

        return ResponseEntity.ok(new WordResponse(message));
    }

    public int getLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
@PostMapping("/check2")
public ResponseEntity<?> check2(@RequestBody WordRequest request) {
    String word1 = analyze(request.getWord1()); // 발음 분석 적용
    String word2 = analyze(request.getWord2());

    int distance = getLevenshteinDistance(word1, word2);

    String message = "발음 기준 두 단어의 차이는 " + distance + " 입니다.";
    return ResponseEntity.ok(new WordResponse(message));
}


@PostMapping("/analyze")
public ResponseEntity<?> analyze(@RequestBody TextRequest request) {
    RestTemplate restTemplate = new RestTemplate();
    String pythonUrl = "http://파이썬서버주소:5000/analyze";

    Map<String, String> body = Map.of("text", request.getText());
    HttpEntity<Map<String, String>> entity = new HttpEntity<>(body);

    ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, entity, Map.class);
    return ResponseEntity.ok(response.getBody());
}