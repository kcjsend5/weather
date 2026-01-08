package com.app.weather.domain.region.controller;

import com.app.weather.domain.region.dto.request.LocationRequest;
import com.app.weather.domain.region.dto.response.ForecastResponse;
import com.app.weather.domain.region.dto.response.RegionResponse;
import com.app.weather.domain.region.dto.response.ShortForecastResponse;
import com.app.weather.domain.region.dto.response.WeatherResponse;
import com.app.weather.domain.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
@Tag(name = "기능",description = "데이터 반환 및 유저 지역 자동 변경")
public class RegionController {

    private final RegionService service;

    @Operation(summary = "지역 정보",description = "현재 유저의 지역에 대한 정보")
    @GetMapping("/info")
    public ResponseEntity<RegionResponse> getRegionInfo(){
        RegionResponse response = service.getRegionInfo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "날씨 정보",description = "현재 유저의 지역의 현재 날씨에 대한 정보")
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getNowWeather(){
        WeatherResponse response = service.getNowWeather();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "과거 날씨 정보",description = "현재 유저의 지역의 과거 일주일간의 날씨에 대한 정보")
    @GetMapping("/pastweather")
    public ResponseEntity<List<WeatherResponse>> getPastWeather(){
        List<WeatherResponse> responses = service.getPastWeather();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "초단기 예보 정보",description = "현재 유저의 지역의 6시간 내의 날씨 예보에 대한 정보")
    @GetMapping("/shortforecast")
    public ResponseEntity<List<ShortForecastResponse>> getShortForecastInfo(){
        List<ShortForecastResponse> responses = service.getShortForecastInfo();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "단기 예보 정보",description = "현재 유저의 지역의 3일간의 날씨 예보에 대한 정보")
    @GetMapping("/forecast")
    public ResponseEntity<List<ForecastResponse>> getForecastInfo(){
        List<ForecastResponse> responses = service.getForecastInfo();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "지역 업데이트",description = "유저의 현재 좌표를 통해 지역의 중심 좌표와 4KM 이상 떨어지는 경우 자동으로 지역을 탐색 후 변경하는 API")
    @Parameters({
            @Parameter(name = "userLat",description = "유저 위도"),
            @Parameter(name = "userLon",description = "유저 경도")
    })
    @PostMapping("/location")
    public ResponseEntity<Void> updateLocationInfo(@RequestBody LocationRequest request){
        service.updateLocation(request);
        return ResponseEntity.ok().build();
    }

}
