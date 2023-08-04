package com.example.commentservicedemo.error;

import lombok.Getter;

public enum ErrorCode {

    UNKNOWN("UNKNOWN"),
    INVALID("INVALID"),
    NOT_FOUND("NOT_FOUND"),
    BAD_REQUEST("BAD_REQUEST"),
    FAILED_DEPENDENCY("FAILED_DEPENDENCY"),
    UNAUTHORIZED("UNAUTHORIZED"),
    UNPROCESSABLE_ENTITY("UNPROCESSABLE_ENTITY"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");

    @Getter
    private final String name;

    private ErrorCode(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

}
