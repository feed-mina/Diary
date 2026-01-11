package com.domain.demo_backend.token.domain;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    private Long userSqno;
    private String email;

    private String refreshToken;

    private LocalDateTime expiration;

    public RefreshToken(Long userSqno, String email, String refreshToken, LocalDateTime expiration) {
        this.userSqno = userSqno;
        this.email = email;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

}
