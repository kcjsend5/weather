package com.app.weather.global.exception.token;


import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class InvalidTokenException extends CustomException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
