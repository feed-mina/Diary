package com.domain.demo_backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    private String userId;

    @NotBlank(message = "이름은 필수입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    // private String hashedPassword;
    private String role;

    @NotBlank(message = "핸드폰 번호는 필수입니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일을 입력하세요.")
    private String email;

    // private String nickname;
    private LocalDate createdAt;
    private String updatedAt;


    @NotBlank(message = "인등코드는 필수입니다.")
    private String accessToken;
    //이메일 인증 코드

    @NotBlank(message = "이메일 인증코드가 필요합니다.")
    private String code;
}
