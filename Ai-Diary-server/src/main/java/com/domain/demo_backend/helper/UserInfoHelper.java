package com.domain.demo_backend.helper;

import com.domain.demo_backend.util.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;

public class UserInfoHelper {
    // 현재 로그인된 사용자의 정보를 가져온다. @return MemberInfo 현재 사용자의 정보를 가져오는 객체

    public static CustomUserDetails getMemberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
<<<<<<< HEAD
        // 인증 정보 확인 로그 추가
        System.out.println("인증 객체: " + authentication);
=======

>>>>>>> rebase-branch
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }
        // principal의 클래스 정보 로그 추가
        System.out.println("인증 객체의 principal 클래스: " + authentication.getPrincipal().getClass().getName());

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보가 유효하지 않습니다.");
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }


    // 현재 사용자의 userSqno 일련번호를 가져온다. @Return BigIntiger 사용자 일련번호
    public static BigInteger getCurrentUserSeq() {
        return getMemberInfo().getUserSqno();
    }
}
