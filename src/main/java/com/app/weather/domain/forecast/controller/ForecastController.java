package com.app.weather.domain.forecast.controller;

import com.app.weather.domain.forecast.dto.request.ForecastRequest;
import com.app.weather.domain.forecast.service.ForecastService;
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
@RequestMapping("/forecast")
@Tag(name = "단기예보",description = "단기예보 데이터 저장")
public class ForecastController {

    private final ForecastService service;

    @Operation(summary = "단기예보 수동 저장 API",description = "주기적으로 자동 저장되는 단기예보를 수동 저장하는 API")
    @Parameters({
            @Parameter(name = "day",description = "해당 날짜의 단기 예보 저장",example = "20260108"),
            @Parameter(name = "time",description = "해당 시간의 단기 예보 저장(2,5,8,11,14,17,20,23시 10분 데이터 제공)",example = "0210")
    })
    @PostMapping("/set")
    public ResponseEntity<Void> setForecast(@RequestBody ForecastRequest request){
        service.fetchForecastInfo(request.getDay(), request.getTime());
        return ResponseEntity.ok().build();
    }

}
