package com.domain.demo_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("📌 Authorization Header: " + authorizationHeader);

        // ✅ 올바른 Authorization 헤더 검증
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("⛔ JWT 토큰이 존재하지 않거나 올바른 형식이 아닙니다.");
            filterChain.doFilter(request, response); // 다음 필터로 넘김
            return;
        }

        String token = authorizationHeader.substring(7);
        System.out.println("✅ 추출된 JWT 토큰: " + token);

        try {
            // ✅ 토큰 검증
            Claims claims = jwtUtil.validateToken(token);
            System.out.println("✅ claims: " + claims);

            // ✅ 사용자 정보 추출
            String username = claims.getSubject();
            String userId = claims.get("userId", String.class);
            String userSqnoStr = claims.get("userSqno", String.class);
            BigInteger userSqno = new BigInteger(userSqnoStr);

            System.out.println("👤 username: " + username);
            System.out.println("🔑 userId: " + userId);
            System.out.println("🆔 userSqno: " + userSqno);

            if (username != null) {
                // ✅ 사용자 권한 설정
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                // ✅ 사용자 정보 저장
                CustomUserDetails userDetails = new CustomUserDetails(username, userSqno, userId, authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("✅ 사용자 인증 성공: " + authentication);
            }

        } catch (ExpiredJwtException e) {
            System.err.println("⛔ 만료된 JWT 토큰: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Expired JWT Token. Please login again.");
            return;
        } catch (Exception e) {
            System.err.println("⛔ 유효하지 않은 JWT 토큰: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT Token");
            return;
        }

        // ✅ 모든 검증이 끝나면 필터 체인 진행
        filterChain.doFilter(request, response);
    }
}
