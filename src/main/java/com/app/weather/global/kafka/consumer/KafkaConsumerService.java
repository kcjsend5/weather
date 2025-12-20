package com.app.weather.global.kafka.consumer;

import com.app.weather.global.fcm.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final FcmService fcmService;

    @KafkaListener(topics = "weatherTopic")
    public void listen(ConsumerRecords<String,String> records) throws IOException, FirebaseMessagingException {
        for(ConsumerRecord<String,String> record:records){
            String key = record.key();
            String message = record.value();
            fcmService.sendMessageTo(key, message);
        }
    }

    @KafkaListener(topics = "alarmTopic")
    public void listenAlarm(ConsumerRecords<String,String> records) throws IOException, FirebaseMessagingException {
        fcmService.sendMessageToAlarm(records);
    }
}
