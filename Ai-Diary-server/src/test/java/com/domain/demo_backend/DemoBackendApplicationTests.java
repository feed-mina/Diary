package com.domain.demo_backend;

import com.domain.demo_backend.mapper.UserMapper;
import com.domain.demo_backend.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoBackendApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testMapperLoaded() {
        System.out.println("@@@@@@@@@@@@@@@@@@@@testMapperLoaded");
        User user = userMapper.findByUserEmail("test@email.com");
        System.out.println(user);
    }
    @Test
    void contextLoads() {
    }

}
