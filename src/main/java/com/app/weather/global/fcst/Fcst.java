package com.app.weather.global.fcst;

import com.app.weather.domain.region.domain.Region;

import com.app.weather.global.convert.ConvertGPS;
import com.app.weather.global.convert.LatXLngY;
import com.app.weather.global.fcst.dto.FcstResponse;
import com.app.weather.global.fcst.dto.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Fcst {
    private final ConvertGPS convertGPS;
    @Value("${weather.key}")
    private String authKey;
    public List<Item> getApi(Region region, RestClient restClient, String uri){
        LatXLngY xy = convertGPS.convertGRID_GPS(region.getLat(), region.getLon());
        ResponseEntity<FcstResponse> response = restClient.get()
                .uri(uriBuilder->uriBuilder.path(uri)
                        .queryParam("authKey",authKey)
                        .queryParam("base_date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .queryParam("base_time", LocalDate.now().format(DateTimeFormatter.ofPattern("HHmm")))
                        .queryParam("numOfRows", 2000)
                        .queryParam("nx", xy.x)
                        .queryParam("ny", xy.y)
                        .queryParam("dataType", "JSON")
                        .build())
                .retrieve()
                .toEntity(FcstResponse.class);
        FcstResponse body = response.getBody();

        return body.getResponse().getBody().getItems().getItem();
    }
}
