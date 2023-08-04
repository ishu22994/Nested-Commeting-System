package com.example.commentservicedemo.enums;

import lombok.Getter;

public enum ActionEntity {

    POST("POST"),
    COMMENT("COMMENT");

    @Getter
    String value;

    ActionEntity(String value) {
        this.value = value;
    }

}
