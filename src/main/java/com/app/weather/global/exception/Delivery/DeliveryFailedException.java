package com.app.weather.global.exception.Delivery;

import com.app.weather.global.exception.CustomException;
import com.app.weather.global.exception.ErrorCode;

public class DeliveryFailedException extends CustomException {
    public DeliveryFailedException() {
        super(ErrorCode.Delivery_Failed);
    }
}
