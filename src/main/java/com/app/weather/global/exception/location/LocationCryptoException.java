package com.app.weather.global.exception.location;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class LocationCryptoException extends CustomException {
    public LocationCryptoException() {
        super(ErrorCode.LOCATION_CRYPTO);
    }
}
