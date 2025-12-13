package com.app.weather.global.fcst.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FcstResponse {
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
            }

        }
    }

}
