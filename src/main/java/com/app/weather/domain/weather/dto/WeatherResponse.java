package com.app.weather.domain.weather.dto;

import com.app.weather.type.Category;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WeatherResponse {
    private Response response;
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response{
        private Body body;
        @Getter
        @Builder
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor
        public static class Body{
            private String dataType;
            private Items items;
            @Getter
            @Builder
            @NoArgsConstructor(access = AccessLevel.PROTECTED)
            @AllArgsConstructor
            public static class Items{
                private List<Item> item;
                @Getter
                @Builder
                @NoArgsConstructor(access = AccessLevel.PROTECTED)
                @AllArgsConstructor
                public static class Item{
                    private String baseDate;
                    private String baseTime;
                    private Category category;
                    private double nx;
                    private double ny;
                    private String obsrValue;
                }
            }

        }
    }

}
