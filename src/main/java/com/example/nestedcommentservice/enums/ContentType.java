package com.example.nestedcommentservice.enums;

import lombok.Getter;

public enum ContentType {

    POST("POST"),
    COMMENT("COMMENT");

    @Getter
    String value;

    ContentType(String value) {
        this.value = value;
    }

}
