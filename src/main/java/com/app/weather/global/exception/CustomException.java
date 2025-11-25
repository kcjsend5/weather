package com.app.weather.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    protected ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    //에러 메시지
    public CustomException(ErrorCode errorCode,String message){
        super(message);
        this.errorCode = errorCode;
    }

    //에러 원인
    public CustomException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(),cause);
        this.errorCode = errorCode;
    }

}
