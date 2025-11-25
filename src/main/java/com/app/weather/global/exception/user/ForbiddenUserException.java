package com.app.weather.global.exception.user;


import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class ForbiddenUserException extends CustomException {
    public ForbiddenUserException(){
        super(ErrorCode.FORBIDDEN_USER);
    }
}
