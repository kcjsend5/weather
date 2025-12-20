package com.app.weather.global.fcm.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FcmDto {
    private String title;
    private String body;
    private String image;
}
