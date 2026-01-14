package com.domain.demo_backend;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DynamicExecutor {

    public List<Map<String, Object>> executeList(String query, Map<String, Object> params) {
        // 실제로는 여기서 JdbcTemplate 등을 이용해 query를 실행하겠지만,
        // 지금은 데이터가 없으므로 무조건 Mock 데이터를 반환하거나
        // query 내용에 따라 분기 처리를 합니다.

        System.out.println("DynamicExecutor: 가짜 데이터를 생성하여 반환합니다.");

        return createMockDiaryList();
    }

    private
    List<Map<String, Object>> createMockDiaryList() {
        List<Map<String, Object>> mockList = new ArrayList<>();

        // 첫 번째 가짜 일기
        Map<String, Object> diary1 = new HashMap<>();
        diary1.put("diaryId", 1);
        diary1.put("title", "오늘의 공부");
        diary1.put("content", "공통 쿼리 실행기를 만들었다. 뿌듯하다.");
        diary1.put("regDt", "2026-01-15 10:00:00");
        diary1.put("userId", "mina");

        // 두 번째 가짜 일기
        Map<String, Object> diary2 = new HashMap<>();
        diary2.put("diaryId", 2);
        diary2.put("title", "경주 여행 계획");
        diary2.put("content", "혼자 떠나는 경주 여행, 맛집을 찾아봐야지.");
        diary2.put("regDt", "2026-01-15 11:30:00");
        diary2.put("userId", "mina");

        mockList.add(diary1);
        mockList.add(diary2);

        return mockList;
    }
}
