package com.domain.demo_backend.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

// 사용자 정보와 관련된 =도메인 클래스, Builder 패턴사용해 객체 생성, username, password, role 필드 포함

@Setter
@Getter
@NoArgsConstructor
public class User {
    private BigInteger userSqno;         // user_sqno
    private String userId;              // user_id
    private String password;            // password
    private String hashedPassword;      // hashed_password
    private String role;                // role
    private String username;            // username
    private String phone;               // phone
    private String email;               // email
    private String repassword;          // repassword
    private String nickname;
    private String delYn; // 추가된 탈퇴 여부 컬럼

    private String verifyYn; // 'N', 'Y'
    private String socialType; // 'K'
    private String verificationCode; //  인증번호 6숫자

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime withdrawAt;

    private String sleepUsingType;
    private String drugUsingType;

    @Builder
    public User(String userId, String password, String hashedPassword, String role, String username, String delYn, String phone, String email, String verifyYn, String socialType, LocalDateTime createdAt, LocalDateTime updatedAt, String verificationCode, LocalDateTime withdrawAt, String sleepUsingType, String drugUsingType) {
        this.userId = userId;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.username = username;
        this.delYn = delYn;
        this.phone = phone;
        this.email = email;
        this.verifyYn = verifyYn;
        this.socialType = socialType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.verificationCode= verificationCode;
        this.withdrawAt = withdrawAt;
        this.sleepUsingType = sleepUsingType;
        this.drugUsingType = drugUsingType;
    }

}
