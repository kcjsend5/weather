package com.app.weather.global.exception.uuid;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class InvalidUuidException extends CustomException {
    public InvalidUuidException() {
        super(ErrorCode.INVALID_UUID);
    }
}
