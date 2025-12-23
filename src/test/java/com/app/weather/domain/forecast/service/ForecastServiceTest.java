package com.app.weather.domain.forecast.service;

import com.app.weather.domain.forecast.domain.Forecast;
import com.app.weather.domain.forecast.repository.ForecastRepository;
import com.app.weather.domain.region.dto.request.LocationRequest;
import com.app.weather.domain.region.service.RegionService;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.dto.request.SignRequest;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.domain.user.service.UserService;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.jwt.custom.CustomUserDetails;
import jakarta.persistence.EntityManager;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ForecastServiceTest {

    @Autowired
    private ForecastService service;
    @Autowired
    private ForecastRepository repository;
    @Autowired
    private RegionService regionService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
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
        regionService.updateLocation(LocationRequest.builder()
                .userLat(37.98776f)
                .userLon(128.12345f)
                .build());
        service.getForecastInfo();
    }

    @Test
    void deleteForecast(){
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
        service.getForecastInfo();
        service.deleteForecast();
    }
}