package com.app.weather.global.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService implements EventProducerService{

    private final KafkaTemplate<String,String> kafkaTemplate;


    @Override
    public void sendMessage(String key, String message) {
        kafkaTemplate.send("weatherTopic",key,message);
    }

    @Override
    public void sendAlarm(String key, String message){
        kafkaTemplate.send("alarmTopic",key,message);
    }
}
