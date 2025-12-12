package com.app.weather.domain.region.dto.response;

import com.app.weather.domain.measurement.domain.Measurement;
import com.app.weather.type.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WeatherResponse {

    private String baseDate;

    private String baseTime;

    @Builder.Default
    private List<MeasurementResponse> measurements = new ArrayList<>();

}
