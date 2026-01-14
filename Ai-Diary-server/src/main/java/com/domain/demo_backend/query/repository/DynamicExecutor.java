package com.domain.demo_backend.query.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DynamicExecutor {

//    @Autowired
//    private CommonMapper commonMapper;
//
//    public List<Map<String, Object>> executeList(String sql, Map<String, Object> params){
//        return commonMapper.executeDynamicQuery(sql, params);
//    }
// DB 연결은 나중에 할 것이므로 지금은 주석 처리하거나 빼두세요.
    // private CommonMapper commonMapper;

    public List<Map<String, Object>> executeList(String sql, Map<String, Object> params) {
        System.out.println("@@@ DynamicExecutor: 가짜 데이터를 반환합니다.");
        return createMockDiaryList();
    }
    private List<Map<String, Object>> createMockDiaryList() {
        List<Map<String, Object>> mockList = new ArrayList<>();

        Map<String, Object> diary1 = new HashMap<>();
        diary1.put("diaryId", 1);
        diary1.put("title", "오늘의 공부");
        diary1.put("content", "파일 정리를 완료했다.");
        diary1.put("regDt", "2026-01-15 10:00:00");
        diary1.put("userId", "mina");

        Map<String, Object> diary2 = new HashMap<>();
        diary2.put("diaryId", 2);
        diary2.put("title", "테스트 성공");
        diary2.put("content", "이제 에러 없이 서버가 켜질 거야.");
        diary2.put("regDt", "2026-01-15 11:30:00");
        diary2.put("userId", "mina");

        mockList.add(diary1);
        mockList.add(diary2);

        return mockList;
    }
}
