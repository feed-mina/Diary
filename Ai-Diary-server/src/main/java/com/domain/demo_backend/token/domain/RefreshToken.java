package com.domain.demo_backend.token.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    private BigInteger userSqno;
    private String email;

    private String refreshToken;

    private LocalDateTime expiration;
    public RefreshToken() {}

    public RefreshToken(BigInteger userSqno, String email, String refreshToken, LocalDateTime expiration) {
        this.userSqno = userSqno;
        this.email = email;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

}
