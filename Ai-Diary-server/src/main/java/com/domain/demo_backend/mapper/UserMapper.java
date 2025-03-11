package com.domain.demo_backend.mapper;

import com.domain.demo_backend.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    User findByEmail(String email);

    User findByUserPhone(String phone);

    User findByUserEmail(String email);

    void insertUser(User user);

    User findByUsername(String username);


    void updateVerifyYn(String email);
    void nonMember(User user);

    void updateVerificationCode(String email, String verificationCode);
    void deleteVerification(String email);
    void updateUserSocialType(User user);

}
