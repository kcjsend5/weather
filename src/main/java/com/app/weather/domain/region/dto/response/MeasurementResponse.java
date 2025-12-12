package com.app.weather.domain.region.dto.response;

import com.app.weather.type.Category;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MeasurementResponse {

    private Double value;

    private Category category;

}
