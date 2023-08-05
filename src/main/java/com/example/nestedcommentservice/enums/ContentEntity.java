package com.example.nestedcommentservice.enums;

import lombok.Getter;

public enum ContentEntity {

    POST("POST"),
    COMMENT("COMMENT");

    @Getter
    String value;

    ContentEntity(String value) {
        this.value = value;
    }

}
