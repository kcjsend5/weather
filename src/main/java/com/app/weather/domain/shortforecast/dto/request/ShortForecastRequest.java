package com.app.weather.domain.shortforecast.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShortForecastRequest {
    private String day;
    private String time;
}
