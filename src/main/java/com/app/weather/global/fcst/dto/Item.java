package com.app.weather.global.fcst.dto;

import com.app.weather.type.Category;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Item {
    private String fcstDate;
    private String fcstTime;
    private Category category;
    private double nx;
    private double ny;
    private String fcstValue;
}
