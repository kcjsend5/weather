package com.app.weather.global.exception.token;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class TokenNotFoundExeption extends CustomException {
    public TokenNotFoundExeption() {
        super(ErrorCode.TOKEN_NOT_FOUND);
    }
}
