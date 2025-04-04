package com.domain.demo_backend.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAuthResponse {
    private KakaoUserInfo kakaoUserInfo;
    private String jwtToken;

}
