package com.example.nestedcommentservice.enums;

import lombok.Getter;

public enum Action {

    LIKE("LIKE"),
    DISLIKE("DISLIKE");

    @Getter
    String value;

    Action(String value) {
        this.value = value;
    }

}
