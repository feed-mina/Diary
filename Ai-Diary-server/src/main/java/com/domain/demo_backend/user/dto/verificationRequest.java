package com.domain.demo_backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class verificationRequest {
    private String Email;
    private String code;
}
