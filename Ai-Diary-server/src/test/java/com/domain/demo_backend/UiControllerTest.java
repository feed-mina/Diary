package com.domain.demo_backend;

import com.domain.demo_backend.controller.UiController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그인 페이지 메타데이터를 성공적으로 가져오는 테스트")
    void getLoginUiMetadataTest() throws Exception{
        mockMvc.perform(get("/api/ui/LOGIN_PAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success")) // 응답구조확인
                .andExpect(jsonPath("$.data[0].screenId").value("LOGIN_PAGE"))
                .andExpect(jsonPath("$.data.length()").isNotEmpty());
    }
}
