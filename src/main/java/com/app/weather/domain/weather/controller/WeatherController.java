package com.app.weather.domain.weather.controller;

import com.app.weather.domain.weather.domain.request.WeatherRequest;
import com.app.weather.domain.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
@Tag(name = "날씨실황",description = "날씨실황 데이터 저장")
public class WeatherController {

    private final WeatherService service;

    @Operation(summary = "날씨실황 수동 저장 API",description = "주기적으로 자동 저장되는 날씨실황을 수동 저장하는 API")
    @Parameters({
            @Parameter(name = "day",description = "해당 날짜의 초단기 실황 저장",example = "20260108"),
            @Parameter(name = "time",description = "해당 시간의 초단기 실황 저장(매시 10분 데이터 제공)",example = "0210")
    })
    @PostMapping("/set")
    public ResponseEntity<Void> setWeather(@RequestBody WeatherRequest request){
        service.fetchWeatherInfo(request.getDay(), request.getTime());
        return ResponseEntity.ok().build();
    }

}
