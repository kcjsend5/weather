package com.app.weather.domain.forecast.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ItemTuple {
    private String fcstDate;
    private String fcstTime;
}
