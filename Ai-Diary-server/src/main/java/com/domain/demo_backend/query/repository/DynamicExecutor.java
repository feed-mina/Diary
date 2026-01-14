package com.domain.demo_backend.query.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DynamicExecutor {

    @Autowired
    private CommonMapper commonMapper;
    public List<Map<String, Object>> executeList(String sql, Map<String, Object> params){
        return commonMapper.executeDynamicQuery(sql, params);
    }
}
