package com.app.weather.global.convert;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LatXLngY {

    public double lat;
    public double lng;

    public double x;
    public double y;
}
