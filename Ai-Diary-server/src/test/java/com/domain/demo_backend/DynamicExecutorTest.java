package com.domain.demo_backend;
import com.domain.demo_backend.query.repository.DynamicExecutor;
import com.domain.demo_backend.query.repository.CommonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

//2026.01.15 Mockito를 사용해 다이어리 페이지 테스트
@ExtendWith(MockitoExtension.class)
public class DynamicExecutorTest {

    @Mock
    private CommonMapper commonManager;

    @InjectMocks
    private DynamicExecutor dynamicExecutor;

    @Test
    @DisplayName("CommonMapper가 가짜 데이터를 반환 DynamicExecutor 테스트")
    public void executeListTest(){
        // 1.준비 (Given)
        List<Map<String, Object>> mockResult = new ArrayList<>();
        Map<String ,Object> data = new HashMap<>();
        data.put("title", "오늘의 공부");
        mockResult.add(data);
        // commonMapper에게  SQL과 파라미터를 받아도  mockResult를 받도록 설정
//        when(commonManager.executeDynamicQuery(anyString(), anyMap())).thenReturn(mockResult);

        // 2, 살향 (when)
        List<Map<String, Object>> result = dynamicExecutor.executeList("DUMMY SQL",new HashMap<>());

        // 3. 검증 (Then)
        assertThat(result).isNotEmpty();

        assertThat(result.get(0).get("title")).isEqualTo("오늘의 공부");
        System.out.println("테스트 성공! 반환된 데이터: " + result);
    }

}
