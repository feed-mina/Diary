package com.domain.demo_backend.util;

import com.domain.demo_backend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey hmacSecretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        String key = jwtProperties.getSecretKey();
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("❗ jwt.secret-key 값이 없습니다!");
        }

        this.hmacSecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
    }

    // JWT 토큰 생성 메서드
    public String createToken(String email, String hashedPassword, String userId) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setClaims(claims)
                .claim("email", email)
                .claim("hashedPassword", hashedPassword)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(hmacSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰 검증 메서드
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(hmacSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔒 AES-256 관련 기능 --------------------------

    private static final String SECRET_KEY = "thisIsMySuperLongSecretKeyForJwt2025!!!!"; // 암호화용
    private static final String SALT = "mySaltValue";

    public static String encryptAesByBase64(String strToEncrypt) {
        try {
            byte[] iv = new byte[16];
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            byte[] encryptedText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptAesByBase64(String strToDecrypt) {
        try {
            byte[] iv = new byte[16];
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

            byte[] decodedText = Base64.getDecoder().decode(strToDecrypt);
            return new String(cipher.doFinal(decodedText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SecretKeySpec getSecretKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }
}
