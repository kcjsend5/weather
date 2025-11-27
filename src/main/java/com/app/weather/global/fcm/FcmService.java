package com.app.weather.global.fcm;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface FcmService {
    int sendMessageTo(String key, String message) throws IOException;
}
