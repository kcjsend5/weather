package com.app.weather.domain.region.dto.response;

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

    private int baseDate;

    private int baseTime;

    @Builder.Default
    private List<Integer> obsrValues = new ArrayList<>();

    @Builder.Default
    private List<String> categories = new ArrayList<>();

}
