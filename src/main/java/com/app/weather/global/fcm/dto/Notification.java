package com.app.weather.global.fcm.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    private String title;
    private String body;
    private String image;
}
