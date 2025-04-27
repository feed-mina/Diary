package com.domain.demo_backend.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordResponse {
    private String message;

    public WordResponse(String message) {
        this.message = message;
    }
    // getter
}
