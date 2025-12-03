package com.app.weather.domain.region.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LocationRequest {

    private Float userLat;
    private Float userLon;
}
