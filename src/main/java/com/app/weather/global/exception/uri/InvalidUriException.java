package com.app.weather.global.exception.uri;


import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class InvalidUriException extends CustomException {
    public InvalidUriException() {
        super(ErrorCode.INVALID_URI);
    }
}
