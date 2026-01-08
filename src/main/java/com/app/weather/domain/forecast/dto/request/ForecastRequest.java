package com.app.weather.domain.forecast.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ForecastRequest {

    private String day;
    private String time;

}
