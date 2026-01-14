package com.domain.demo_backend.query.controller;

import com.domain.demo_backend.query.repository.DynamicExecutor;
import com.domain.demo_backend.query.service.QueryMasterService;
import com.domain.demo_backend.util.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/execute")
public class CommonQueryController {
    private final QueryMasterService queryMasterService;
    private final DynamicExecutor dynamicExecutor; // 실행기 추가

    @Autowired
    public CommonQueryController(QueryMasterService queryMasterService, DynamicExecutor dynamicExecutor) {
        this.queryMasterService = queryMasterService;
        this.dynamicExecutor = dynamicExecutor;
    }

    @PostMapping("/{sqlKey}")
    public ResponseEntity<?> execute(@PathVariable String sqlKey, @RequestBody Map<String, Object> params, Authentication authentication){
        System.out.println("@@@ 공통 실행기 진입 sqlkey: " + sqlKey);

        // 서비스 로부터 SQL 설계도(Query text)를 가져온다. (Redis 또는 DB)
        String query = queryMasterService.getQuery(sqlKey);

        // 로그인한 사용자의 정보를 파라미터에 자동으로 추가
        // 프론트에서 실수로 빼먹더라도 서버에서 보안상 안전하게 넣는다.
        if (authentication != null){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            params.put("userSqno", userDetails.getUserSqno()); // 토큰의 sqno를 파라미터에 주입
            params.put("userId", userDetails.getUserId());
        }

        // 실제 실행 : 로그로 쿼리가 잘 가져옸는지 확인
        System.out.println("@@@ 실행할 쿼리 : "+ query);
        System.out.println("@@@ 바인딩할 파라미터:"+ params);

        // todo : 동적 쿼리 실행기 (DynamicExecutor) 호출 로직이 들어간다

        List<Map<String, Object>> result = dynamicExecutor.executeList(query, params);

        return ResponseEntity.ok().body(Map.of(
                "status", "success", "sqlKey", sqlKey, "data", result
        ));
    }
}
