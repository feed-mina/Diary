package com.domain.demo_backend;

import com.domain.demo_backend.config.JwtProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@MapperScan("com.domain.demo_backend.mapper")
public class DemoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBackendApplication.class, args);
    }

}
