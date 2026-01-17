package com.domain.demo_backend.util;

import io.jsonwebtoken.Claims;
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


//    cloundfront 적용 후 프록시 설정으로 추가
    private static final List<String> EXCLUDE_URLS = List.of(
            "/api/auth/login",
            "/api/kakao/login"
    );
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_URLS.stream().anyMatch(path::startsWith);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("@@@@Authorization Header: " + authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            System.out.println("@@@@token : " + token);
            try {
                Claims claims = jwtUtil.validateToken(token); // 토큰 검증
                // 유효하지 않은 토큰 예외 처리
                System.out.println("@@@@claims: " + claims);
                String email = claims.getSubject();
                System.out.println("@@@@email : " + email);
                String userId = claims.get("userId", String.class);
                Long userSqno =claims.get("userSqno", Long.class);
                System.out.println("@@@@실제 userSqno : " + userSqno);

                if (email != null) {
                    List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");
                    System.out.println("@@@@authorities: " + authorities);
                    CustomUserDetails userDetails = new CustomUserDetails(email, userSqno, userId, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                    System.out.println("@@@@userDetails: " + userDetails);
                    // 인증 토큰 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    System.out.println("@@@@authentication: " + authentication);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 유효하지 않은 토큰 예외 처리
                System.err.println("Invalid JWT token: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답 반환
                response.getWriter().write("Invalid JWT Token");
                return;
            }
        }
        filterChain.doFilter(request, response); // 다음 필터로 이동
        System.out.println("@@@@request: " + request);
        System.out.println("@@@@response: " + response);
    }


}
