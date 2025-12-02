package com.app.weather.domain.region.service;

import com.app.weather.domain.forecast.domain.Forecast;
import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.region.dto.request.LocationRequest;
import com.app.weather.domain.region.dto.response.ForecastResponse;
import com.app.weather.domain.region.dto.response.RegionResponse;
import com.app.weather.domain.region.dto.response.ShortForecastResponse;
import com.app.weather.domain.region.dto.response.WeatherResponse;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.shortforecast.domain.ShortForecast;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.domain.weather.domain.Weather;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.util.SecurityUtil;
import com.app.weather.type.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository repository;
    private final UserRepository userRepository;

    // 메소드: 지역 특보 실시간 알림

    public RegionResponse getRegionInfo(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();

        return RegionResponse.builder()
                .regionName(region.getName())
                .lat(region.getLat())
                .lon(region.getLon())
                .build();
    }

    public WeatherResponse getNowWeather(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        Weather weather = region.getWeathers().getLast();
        List<String> categories = weather.getCategories().stream().map(Category::getName).toList();
        return WeatherResponse.builder()
                .baseDate(weather.getBaseDate())
                .baseTime(weather.getBaseTime())
                .obsrValues(weather.getObsrValues())
                .categories(categories)
                .build();
    }

    public List<WeatherResponse> getPastWeather(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        List<Weather> weathers = region.getWeathers();

        return weathers.stream().map(w->WeatherResponse.builder()
                    .baseTime(w.getBaseTime())
                    .baseDate(w.getBaseDate())
                    .obsrValues(w.getObsrValues())
                    .categories(w.getCategories().stream().map(Category::getName).toList())
                    .build()
        ).toList();
    }

    public List<ShortForecastResponse> getShortForecastInfo(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        List<ShortForecast> shortForecast = region.getShortForecasts();

        return shortForecast.stream()
                .map(s->ShortForecastResponse.builder()
                        .fcstDate(s.getFcstDate())
                        .fcstTime(s.getFcstTime())
                        .fcstValues(s.getFcstValues())
                        .categories(s.getCategories().stream().map(Category::getName).toList())
                        .build()
                ).toList();
    }

    public List<ForecastResponse> getForecastInfo(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        List<Forecast> Forecast = region.getForecasts();

        return Forecast.stream()
                .map(s->ForecastResponse.builder()
                        .fcstDate(s.getFcstDate())
                        .fcstTime(s.getFcstTime())
                        .fcstValues(s.getFcstValues())
                        .categories(s.getCategories().stream().map(Category::getName).toList())
                        .build()
                ).toList();
    }

    //사용자 위치에 따른 지역 변경
    public void updateLocation(LocationRequest request){

    }


}
