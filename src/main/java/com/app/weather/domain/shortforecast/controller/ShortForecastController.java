package com.app.weather.domain.shortforecast.controller;

import com.app.weather.domain.shortforecast.dto.request.ShortForecastRequest;
import com.app.weather.domain.shortforecast.service.ShortForecastService;
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
@RequestMapping("/shortForecast")
@Tag(name = "초단기예보",description = "초단기예보 데이터 저장")
public class ShortForecastController {

    private final ShortForecastService service;

    @Operation(summary = "초단기예보 수동 저장 API",description = "주기적으로 자동 저장되는 초단기예보를 수동 저장하는 API")
    @Parameters({
            @Parameter(name = "day",description = "해당 날짜의 단기 예보 저장",example = "20260108"),
            @Parameter(name = "time",description = "해당 시간의 단기 예보 저장(매시 45분 데이터 제공)",example = "0245")
    })
    @PostMapping("/set")
    public ResponseEntity<Void> setShortForecast(@RequestBody ShortForecastRequest request){
        service.fetchShortForecastInfo(request.getDay(), request.getTime());
        return ResponseEntity.ok().build();
    }

}
