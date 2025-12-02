package com.app.weather.domain.region.controller;

import com.app.weather.domain.region.dto.response.ForecastResponse;
import com.app.weather.domain.region.dto.response.RegionResponse;
import com.app.weather.domain.region.dto.response.ShortForecastResponse;
import com.app.weather.domain.region.dto.response.WeatherResponse;
import com.app.weather.domain.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService service;

    @GetMapping("/info")
    public ResponseEntity<RegionResponse> getRegionInfo(){
        RegionResponse response = service.getRegionInfo();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getNowWeather(){
        WeatherResponse response = service.getNowWeather();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pastweather")
    public ResponseEntity<List<WeatherResponse>> getPastWeather(){
        List<WeatherResponse> responses = service.getPastWeather();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/shortforecast")
    public ResponseEntity<List<ShortForecastResponse>> getShortForecastInfo(){
        List<ShortForecastResponse> responses = service.getShortForecastInfo();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/forecast")
    public ResponseEntity<List<ForecastResponse>> getForecastInfo(){
        List<ForecastResponse> responses = service.getForecastInfo();
        return ResponseEntity.ok(responses);
    }

}
