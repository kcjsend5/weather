package com.app.weather.domain.weather.domain.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WeatherRequest {
    private String day;
    private String time;
}
