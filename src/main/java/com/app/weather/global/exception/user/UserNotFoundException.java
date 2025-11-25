package com.app.weather.global.exception.user;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
