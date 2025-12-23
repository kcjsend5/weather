package com.app.weather.domain.region.service;

import com.app.weather.domain.forecast.service.ForecastService;
import com.app.weather.domain.region.dto.request.LocationRequest;
import com.app.weather.domain.region.dto.response.ForecastResponse;
import com.app.weather.domain.region.dto.response.RegionResponse;
import com.app.weather.domain.region.dto.response.ShortForecastResponse;
import com.app.weather.domain.region.dto.response.WeatherResponse;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.shortforecast.service.ShortForecastService;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.dto.request.LoginRequest;
import com.app.weather.domain.user.dto.request.SignRequest;
import com.app.weather.domain.user.dto.response.LoginResponse;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.domain.user.service.UserService;
import com.app.weather.domain.weather.service.WeatherService;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.jwt.custom.CustomUserDetails;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class RegionServiceTest {

    @Autowired
    private RegionService service;
    @Autowired
    private RegionRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ForecastService forecastService;
    @Autowired
    private ShortForecastService shortForecastService;
    @Autowired
    private WeatherService weatherService;

    @Test
    void updateLocation(){
        String uuid = "master";
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        LoginResponse response = userService.login(LoginRequest.builder()
                .uuid(uuid)
                .build());
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        service.updateLocation(LocationRequest.builder()//응답 dto 변경하기
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        assertThat(user.getRegion()).isNotNull();
    }

    @Test
    void getRegionInfo(){
        String uuid = "master";
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        LoginResponse response = userService.login(LoginRequest.builder()
                .uuid(uuid)
                .build());
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        service.updateLocation(LocationRequest.builder()//응답 dto 변경하기
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        assertThat(user.getRegion()).isNotNull();
        RegionResponse regionResponse = service.getRegionInfo();
        assertThat(regionResponse).isNotNull();
        assertThat(regionResponse.getRegionName()).isNotNull();
        assertThat(regionResponse.getLat()).isNotNull();
        assertThat(regionResponse.getLon()).isNotNull();
    }

    @Test
    void specialReport(){
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        service.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        service.specialReport();
    }

    @Test
    void getNowWeather(){
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        service.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        weatherService.getWeatherInfo();
        WeatherResponse weatherResponse =service.getNowWeather();
        assertThat(weatherResponse).isNotNull();
    }

    @Test
    void getPastWeather(){
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        service.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        weatherService.getWeatherInfo();
        List<WeatherResponse> weatherResponses = service.getPastWeather();
        assertThat(weatherResponses).isNotEmpty();
    }

    @Test
    void getShortForecastInfo(){
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        service.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        shortForecastService.getShortForecastInfo();
        List<ShortForecastResponse> responses =service.getShortForecastInfo();
        assertThat(responses).isNotEmpty();
    }

    @Disabled
    @Test
    void getForecastInfo(){
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        service.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        forecastService.getForecastInfo();
        List<ForecastResponse> forecastResponses =service.getForecastInfo();
        assertThat(forecastResponses).isNotEmpty();
    }
}