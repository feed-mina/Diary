package com.domain.demo_backend.config;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

@Component
public class MyBatisReadyEventLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void logReady() {
        System.out.println("🟢 MyBatis Mapper 파일들이 로딩되었는지 확인할 준비가 완료되었어요!");
    }
}
