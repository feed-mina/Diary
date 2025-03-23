package com.domain.demo_backend.config;

import com.domain.demo_backend.util.JwtAuthenticationFilter;
import com.domain.demo_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    // 비밀번호 암호화 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll()
//                        .requestMatchers("/api/auth/login", "/api/auth/register", "/public/**").permitAll() // 로그인/회원가입/공개 API 허용
//                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint()) // 🚨 403 Forbidden 반환 (Redirect 방지)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable()) // 🚨 기본 로그인 폼 완전 비활성화
                .logout(logout -> logout.disable()); // 🚨 로그아웃 비활성화 (API 방식 사용)

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:4000",
                "http://web-2025-version1.s3-website.ap-northeast-2.amazonaws.com",
                "https://justsaying.co.kr","http://justsaying.co.kr"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 사용자 정보 관리
//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("testUser")
//                .password(passwordEncoder().encode("testPassword"))
//                .roles("USER")
//                .build());
//        return manager;
//    }


}