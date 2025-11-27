package com.app.weather.global.exception.category;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class InvalidCategoryException extends CustomException {
    public InvalidCategoryException() {
        super(ErrorCode.DUPLICATE_UUID);
    }
}
