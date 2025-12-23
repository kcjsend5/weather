package com.app.weather.domain.region.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GeoResponse {

    private Status status;
    private List<Result> results;

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Status {
        private Integer code;
        private String name;
        private String message;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Result{
        private String name;
        private AddressRegion region;
        @Getter
        @Builder
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor
        public static class AddressRegion{
            private Area area0;
            private Area area1;
            private Area area2;
            private Area area3;
            private Area area4;
            @Getter
            @Builder
            @NoArgsConstructor(access = AccessLevel.PROTECTED)
            @AllArgsConstructor
            public static class Area{
                private String name;
                private Coords coords;
                @Getter
                @Builder
                @NoArgsConstructor(access = AccessLevel.PROTECTED)
                @AllArgsConstructor
                public static class Coords{
                    private Center center;
                    @Getter
                    @Builder
                    @NoArgsConstructor(access = AccessLevel.PROTECTED)
                    @AllArgsConstructor
                    public static class Center{
                        private String crs;
                        private Float x;
                        private Float y;
                    }
                }
            }
        }
    }
}
