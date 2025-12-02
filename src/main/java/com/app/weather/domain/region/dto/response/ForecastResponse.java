package com.app.weather.domain.region.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ForecastResponse {

    private int fcstDate;

    private int fcstTime;

    @Builder.Default
    private List<Integer> fcstValues = new ArrayList<>();

    @Builder.Default
    private List<String> categories = new ArrayList<>();
}
