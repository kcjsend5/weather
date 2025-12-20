package com.app.weather.global.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface FcmService {
    int sendMessageTo(String key, String message) throws IOException, FirebaseMessagingException;
    int sendMessageToAlarm(ConsumerRecords<String,String> records) throws IOException, FirebaseMessagingException;
}
