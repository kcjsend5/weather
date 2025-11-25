package com.app.weather.global.exception.uuid;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class DuplicateUuidException extends CustomException {
    public DuplicateUuidException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}
