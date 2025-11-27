package com.app.weather.global.kafka.producer;

import org.springframework.stereotype.Service;

@Service
public interface EventProducerService {

    public void sendMessage(String key,String message);

}
