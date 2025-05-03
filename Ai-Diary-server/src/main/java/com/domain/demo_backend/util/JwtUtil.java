package com.domain.demo_backend.util;

import com.domain.demo_backend.mapper.UserMapper;
import com.domain.demo_backend.token.domain.RefreshToken;
import com.domain.demo_backend.token.domain.RefreshTokenRepository;
import com.domain.demo_backend.token.domain.TokenResponse;
import com.domain.demo_backend.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    private SecretKey secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret-key}")
    private String secret;

//    private static final String SCRET_kEY = "${jwt.secret-key}";

    private static final String SECRET_KEY = "mySuperSecretKey"; //  비밀키 (32바이트)
    private static final String SALT = "mySaltValue"; //  SALT 값
    private final RefreshTokenRepository refreshTokenRepository;
    // Access Token: 1시간
    private static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간

    // Refresh Token: 7일
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 7일

    private final UserMapper userMapper;
    public JwtUtil(UserMapper userMapper,RefreshTokenRepository refreshTokenRepository) {
        this.userMapper = userMapper;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        System.out.println("issuer 값: " + issuer);

    }

    // 토큰생성
    public TokenResponse generateTokens(String email, String hashedPassword, String userId) {
        Date now = new Date();

        Date accessExp = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);
        Date refreshExp = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY);

        Claims claims = Jwts.claims().setSubject(email);


        if (email == null) {
            throw new IllegalArgumentException("unique_userId값이 존재하지 않습니다.");
        }

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .claim("email", email) // 사용자 고유 식별자 추가
                .claim("hashedPassword", hashedPassword)
                .claim("userId", userId) //
                .setIssuedAt(now)
                .setExpiration(accessExp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(refreshExp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        User user = userMapper.findByUserEmail(email); // 꼭 이 시점에 조회해야 userSqno 나옴!
        refreshTokenRepository.save(new RefreshToken(
                user.getUserSqno(), //  꼭 넣어줘야 해!
                email,
                refreshToken,
                refreshExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() // 한국날짜로 변환
        ));

        return new TokenResponse(accessToken, refreshToken);
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * AES-256으로 문자열을 암호화 하는 메서드
     */
    public static String encryptAesByBase64(String strToEncrypt) {
        try {
            byte[] iv = new byte[16]; // 초기화 백터 (IV)
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            byte[] encryptedText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

            //Base64 인코딩 후 반환
            return Base64.getEncoder().encodeToString(encryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // AES56으로 암호화된 문자열을 복호화 하는 메서드
    public static String decryptAesByBase64(String strToDecrypt) {
        try {
            byte[] iv = new byte[16]; // 초기화 백터 (IV)
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

            // Base64 디코딩 후 복호화
            byte[] decodedText = Base64.getDecoder().decode(strToDecrypt);
            return new String(cipher.doFinal(decodedText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //      * AES-256 비밀키 생성 메서드
    private static SecretKeySpec getSecretKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }


    public String createAccessToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date accessExp = new Date(now.getTime() + 1000L * 60 * 60); // 1시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessExp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
