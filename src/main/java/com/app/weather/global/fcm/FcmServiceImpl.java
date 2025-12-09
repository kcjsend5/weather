package com.app.weather.global.fcm;

import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.user.domain.User;
import com.app.weather.global.exception.delivery.DeliveryFailedException;
import com.app.weather.global.fcm.dto.FcmDto;
import com.app.weather.global.fcm.dto.Message;
import com.app.weather.global.fcm.dto.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmServiceImpl implements FcmService{

    private final RegionRepository repository;

    @Override
    public int sendMessageTo(String key, String message) throws IOException {

        Region region = repository.findByName(key).orElseThrow();
        List<User> users = region.getUsers();
        String URL = "<https://fcm.googleapis.com/v1/projects/weather-515df/messages:send>";
        for(User user: users){
            if(user.getToken()==null||user.getToken().isBlank()){continue;}
            FcmDto fcmDto = makeMessage(user.getToken(),message);
            RestClient restClient = RestClient.create();
            ResponseEntity<Void> response = restClient
                    .post()
                    .uri(URL)
                    .contentType(MediaType.parseMediaType("application/json; charset=UTF-8"))
                    .header("Authorization",  "Bearer " + getAccessToken())
                    .body(fcmDto)
                    .retrieve()
                    .toBodilessEntity();
            log.info(response.getStatusCode().toString());
            if(!response.getStatusCode().is2xxSuccessful()){
                throw new DeliveryFailedException();
            }
        }
        return 0;
    }

    private String getAccessToken() throws IOException{

        String firebaseConfigPath = "firebase/weather-515df-firebase-adminsdk-fbsvc-7237ce1c1c.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    private FcmDto makeMessage(String token,String message) throws JsonProcessingException {

        return FcmDto.builder()
                .message(Message.builder()
                        .token(token)
                        .notification(Notification.builder()
                                .title(message.split(",")[0])
                                .body(message.split(",")[1])
                                .image(null)
                                .build())
                        .build())
                .build();
    }
}
