package com.app.weather.global.fcst;

import com.app.weather.domain.region.domain.Region;

import com.app.weather.global.convert.ConvertGPS;
import com.app.weather.global.convert.LatXLngY;
import com.app.weather.global.fcst.dto.FcstResponse;
import com.app.weather.global.fcst.dto.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Fcst {
    private final ConvertGPS convertGPS;
    @Value("${weather.key}")
    private String authKey;
    public List<Item> getApi(Region region, RestClient restClient, String scheme,String host,String path,String day,String time){
        LatXLngY xy = convertGPS.convertGRID_GPS(region.getLat(), region.getLon());
        ResponseEntity<FcstResponse> response = restClient.get()
                .uri(uriBuilder->uriBuilder
                        .scheme(scheme)
                        .host(host)
                        .path(path)
                        .queryParam("authKey",authKey)
                        .queryParam("base_date", day)
                        .queryParam("base_time", time)
                        .queryParam("numOfRows", 2000)
                        .queryParam("nx", (int)xy.x)
                        .queryParam("ny", (int)xy.y)
                        .queryParam("dataType", "JSON")
                        .build())
                .retrieve()
                .toEntity(FcstResponse.class);
        FcstResponse body = response.getBody();

        return body.getResponse().getBody().getItems().getItem();
    }
}
