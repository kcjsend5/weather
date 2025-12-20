package com.app.weather.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFcm() throws IOException {
        String firebaseConfigPath = "firebase/weather-515df-firebase-adminsdk-fbsvc-7237ce1c1c.json";
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials
                        .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream()))
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseMessaging initFirebaseMessaging(FirebaseApp firebaseApp){
        return FirebaseMessaging.getInstance(firebaseApp);
    }

}
