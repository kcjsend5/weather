package com.app.weather.domain.region.dto.response;

import com.app.weather.domain.measurement.domain.Measurement;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShortForecastResponse {

    private int fcstDate;

    private int fcstTime;

    @Builder.Default
    private List<MeasurementResponse> measurements = new ArrayList<>();

}
