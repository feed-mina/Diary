package com.domain.demo_backend.controller;

import com.domain.demo_backend.ui.domain.UiMetadata;
import com.domain.demo_backend.ui.domain.UiMetadataRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ui")
public class UiController {

    private final UiMetadataRepository uiMetadataRepository;

    public UiController(UiMetadataRepository uiMetadataRepository) {
        this.uiMetadataRepository = uiMetadataRepository;
    }

    @GetMapping("/{uiId}")
    public List<UiMetadata> getUiMetadataList(String uiId){
        return uiMetadataRepository.findByUiIdOrderBySortOrderAsc(uiId);
    }

}
