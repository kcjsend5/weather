package com.app.weather.global.fcm;

import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.global.exception.delivery.DeliveryFailedException;
import com.app.weather.global.exception.region.RegionNotFoundException;
import com.app.weather.global.exception.token.TokenNotFoundExeption;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.fcm.dto.FcmDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmServiceImpl implements FcmService{

    private final RegionRepository repository;
    private final UserRepository userRepository;

    @Override
    public int sendMessageTo(String key, String message) throws IOException, FirebaseMessagingException {

        Region region = repository.findByName(key).orElseThrow(RegionNotFoundException::new);
        List<String> tokenList = region.getUsers().stream().map(User::getToken).toList();
        if(!tokenList.isEmpty()){
            FcmDto fcmDto = makeMessage(message);
            List<String> failedTokens = new ArrayList<>();
            int batchSize = 500;
            for(int i = 0; i<tokenList.size();i+=batchSize){
                List<String> tokens = tokenList.subList(i, Math.min(i + batchSize, tokenList.size()));
                MulticastMessage m = MulticastMessage.builder()
                        .setNotification(Notification.builder()
                                .setTitle(fcmDto.getTitle())
                                .setBody(fcmDto.getBody())
                                .build())
                        .addAllTokens(tokens)
                        .build();
                BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(m,true);
                if(response.getFailureCount()>0){
                    List<SendResponse> responses = response.getResponses();
                    for(int j = 0; j<responses.size();j++){
                        if(!responses.get(j).isSuccessful()){
                            failedTokens.add(tokenList.get(j));
                        }
                    }
                }
            }
            log.warn("List of tokens that caused failures: {}", failedTokens);
        }
        return 0;
    }

    @Override
    public int sendMessageToAlarm(ConsumerRecords<String,String> records) throws IOException, FirebaseMessagingException {

        List<Message> messages = new ArrayList<>();
        for(ConsumerRecord<String,String> record:records){
            String uuid = record.key();
            String message = record.value();
            User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
            if(user.getToken()==null||user.getToken().isBlank()){
                log.warn("token not found userId = {}, token = {}",user.getId(),user.getToken());
                continue;
            }
            FcmDto fcmDto = makeMessage(message);
            Message m = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(fcmDto.getTitle())
                            .setBody(fcmDto.getBody())
                            .build())
                    .setToken(user.getToken())
                    .build();
            messages.add(m);
        }
        int batchSize = 500;
        for (int i = 0; i < messages.size(); i += batchSize) {
            List<Message> batch = messages.subList(i, Math.min(i + batchSize, messages.size()));
            BatchResponse response = FirebaseMessaging.getInstance().sendEach(batch,true);
            log.info(response.getSuccessCount() + " messages were sent successfully in batch " + (i / batchSize + 1));
        }
        return 0;
    }

    private FcmDto makeMessage(String message) throws JsonProcessingException {
        String[] m = message.split("\\|");
        return FcmDto.builder()
                .title(m[0])
                .body(m[1])
                .image(null)
                .build();
    }
}
