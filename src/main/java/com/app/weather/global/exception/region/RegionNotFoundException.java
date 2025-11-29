package com.app.weather.global.exception.region;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class RegionNotFoundException extends CustomException {
    public RegionNotFoundException() {
        super(ErrorCode.REGION_NOT_FOUND);
    }
}
