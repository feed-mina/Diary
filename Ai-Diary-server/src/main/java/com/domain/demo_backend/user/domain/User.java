package com.domain.demo_backend.user.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

// 사용자 정보와 관련된 =도메인 클래스, Builder 패턴사용해 객체 생성, username, password, role 필드 포함

@Setter
@Getter
@AllArgsConstructor
@Builder
public class User {
    private BigInteger userSqno;         // user_sqno
    private String userId;              // user_id
    @NotBlank
    private String password;            // password
    private String hashedPassword;      // hashed_password
    private String role;                // role
    private String username;            // username
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자여야 합니다.")
    private String phone;               // phone
    @NotBlank
    private String email;               // email
    private String repassword;          // repassword
    private String nickname;
    private String delYn; // 추가된 탈퇴 여부 컬럼

    private String verifyYn; // 'N', 'Y'
    private String socialType; // 'K'
    private String verificationCode; //  인증번호 6숫자

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Builder
    public User(String userId, String password, String hashedPassword, String role, String username, String phone, String email, String verifyYn, String socialType, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.verifyYn = verifyYn;
        this.socialType = socialType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
