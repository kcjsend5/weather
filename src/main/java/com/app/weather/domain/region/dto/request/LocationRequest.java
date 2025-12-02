package com.app.weather.domain.region.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LocationRequest {

    private double userLat;
    private double userLon;
}
