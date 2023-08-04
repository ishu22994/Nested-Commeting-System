package com.example.commentservicedemo.enums;

import lombok.Getter;

public enum Action {

    LIKE("LIKE"),
    //DISSLIKE
    DISLIKE("DISLIKE");

    @Getter
    String value;

    Action(String value) {
        this.value = value;
    }

}
