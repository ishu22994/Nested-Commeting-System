package com.example.commentservicedemo.error;

import lombok.Getter;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Getter
    private ErrorCode code;

    public CustomException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public CustomException(){

    }


}