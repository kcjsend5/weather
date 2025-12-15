package com.app.weather.global.kafka.producer;

import org.springframework.stereotype.Service;

@Service
public interface EventProducerService {

    void sendMessage(String key,String message);
    void sendAlarm(String key, String message);

}
