package com.app.weather.global.fcm.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    private Notification notification;
    private String token;
}
