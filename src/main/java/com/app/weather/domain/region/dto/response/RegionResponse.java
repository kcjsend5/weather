package com.app.weather.domain.region.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegionResponse {

    private String regionName;

    private double lat;

    private double lon;

}
