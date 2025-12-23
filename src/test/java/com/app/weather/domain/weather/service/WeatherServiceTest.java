package com.app.weather.domain.weather.service;

import com.app.weather.domain.region.dto.request.LocationRequest;
import com.app.weather.domain.region.service.RegionService;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.dto.request.SignRequest;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.domain.user.service.UserService;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.jwt.custom.CustomUserDetails;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class WeatherServiceTest {

    @Autowired
    private RegionService regionService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WeatherService weatherService;

    @Transactional
    @Test
    void getWeatherInfo(){
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
        regionService.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        weatherService.getWeatherInfo();
    }

    @Transactional
    @Test
    void sendAlarm(){
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
        regionService.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        weatherService.getWeatherInfo();

        userService.setTemperature();
        userService.setWind();
        userService.setRain();
        assertThat(user.isTemperature()).isTrue();
        assertThat(user.isRain()).isTrue();
        assertThat(user.isWind()).isTrue();

        weatherService.sendAlarm();

        userService.unsetTemperature();
        userService.unsetWind();
        userService.unsetRain();
        assertThat(user.isTemperature()).isFalse();
        assertThat(user.isRain()).isFalse();
        assertThat(user.isWind()).isFalse();
    }

    @Test
    void resetAlarm(){
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
        regionService.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        weatherService.getWeatherInfo();
        weatherService.sendAlarm();
        weatherService.resetAlarm();
    }

    @Disabled
    @Test
    void sendWeather(){
        String uuid = UUID.randomUUID().toString();
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
        regionService.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        weatherService.getWeatherInfo();
        weatherService.sendWeather();
    }

    @Test
    void deleteWeather(){
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
        regionService.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        weatherService.getWeatherInfo();
        weatherService.deleteWeather();
    }
}