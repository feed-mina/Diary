package com.domain.demo_backend.user.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoUserInfo {
    private Long id;  // 카카오 유저 ID
    private String connectedAt;
    private String nickname;
    private String email;
    private boolean hasEmail;
    private boolean isEmailValid;
    private boolean isEmailVerified;
    private boolean hasAgeRange;
    private boolean hasBirthday;
    private boolean hasGender;


    public static KakaoUserInfo fromMap(Map<String, Object> body) {
        if (body == null) {
            throw new RuntimeException("카카오에서 사용자 정보를 받지 못했어요!");
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) body.getOrDefault("kakao_account", new HashMap<>());
        Map<String, Object> properties = (Map<String, Object>) body.getOrDefault("properties", new HashMap<>());

        return KakaoUserInfo.builder()
                .id(body.get("id") != null ? ((Number) body.get("id")).longValue() : null)
                .connectedAt((String) body.getOrDefault("connected_at", ""))
                .nickname((String) properties.getOrDefault("nickname", "카카오 사용자"))
                .email((String) kakaoAccount.getOrDefault("email", ""))
                .hasEmail((Boolean) kakaoAccount.getOrDefault("has_email", false))
                .isEmailValid((Boolean) kakaoAccount.getOrDefault("is_email_valid", false))
                .isEmailVerified((Boolean) kakaoAccount.getOrDefault("is_email_verified", false))
                .hasAgeRange((Boolean) kakaoAccount.getOrDefault("has_age_range", false))
                .hasBirthday((Boolean) kakaoAccount.getOrDefault("has_birthday", false))
                .hasGender((Boolean) kakaoAccount.getOrDefault("has_gender", false))
                .build();
    }

}
