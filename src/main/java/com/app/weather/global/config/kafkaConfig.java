package com.app.weather.global.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class kafkaConfig {

    @Bean
    public NewTopic weatherTopic(){
        return TopicBuilder.name("weatherTopic")
                .replicas(1)                             // 복제 팩터 설정 (1)
                .config(                                            // 추가 설정
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
                )
                .build();
    }
}
