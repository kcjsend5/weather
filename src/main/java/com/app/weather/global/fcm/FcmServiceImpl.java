package com.app.weather.global.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService{
    @Override
    public int sendMessageTo(String key, String message) throws IOException {
        return 0;
    }

    private String getAccessToken() throws IOException{
        return "";
    }

    private String makeMessage(String token,String message) throws JsonProcessingException {
        return "";
    }
}
