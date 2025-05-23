package com.domain.demo_backend.mapper;

import com.domain.demo_backend.user.domain.User;
import jakarta.validation.constraints.Email;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    User findByUserPhone(String phone);
    User findByUserEmail(@Param("email") String email);
    void insertUser(User user);
    User findByUsername(String username);
    void updateUpdatedAt(@Param("email") String email);

    void updateVerifyYn(String email);
    void nonMember(User user);
    void updateVerificationCode(String email, String verificationCode);
    void updateUserSocialType(User user);
    BigInteger findIndexByEmail(String email);

    void editPassword(User existingUser);

    User findWidthdrawUser(@Email(message = "유효한 이메일을 입력하세요.") String email);

    void reactivateUser(User user);
}