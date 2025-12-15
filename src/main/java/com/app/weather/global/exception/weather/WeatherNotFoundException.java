package com.app.weather.global.exception.weather;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class WeatherNotFoundException extends CustomException {
    public WeatherNotFoundException() {
        super(ErrorCode.WEATHER_NOT_FOUND);
    }
}
