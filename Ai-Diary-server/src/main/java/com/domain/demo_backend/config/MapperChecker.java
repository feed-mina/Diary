package com.domain.demo_backend.config;

import com.domain.demo_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MapperChecker implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {
        if (userMapper == null) {
            System.out.println("❌❌ UserMapper가 주입되지 않았어요! XML 파일 확인 필요!");
        } else {
            System.out.println("✅✅ UserMapper가 정상적으로 주입되었어요!");
        }
    }
}
