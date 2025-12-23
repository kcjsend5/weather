package com.app.weather.domain.user.service;

import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.dto.request.LoginRequest;
import com.app.weather.domain.user.dto.request.RefreshTokenRequest;
import com.app.weather.domain.user.dto.request.SignRequest;
import com.app.weather.domain.user.dto.response.LoginResponse;
import com.app.weather.domain.user.dto.response.TokenResponse;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.global.exception.token.InvalidTokenException;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.jwt.JwtTokenProvider;
import com.app.weather.global.jwt.custom.CustomUserDetails;
import com.app.weather.global.util.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository repository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void signUp() {
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        User user = repository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        assertThat(user.getToken()).isEqualTo(token);
    }

    @Test
    void login() {
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        LoginResponse response = userService.login(LoginRequest.builder()
                .uuid(uuid)
                .build());
        assertThat(response).isNotNull();
    }

    @Test
    void recreateToken() {
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        User user = repository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        assertThat(user.getToken()).isEqualTo(token);
        LoginResponse response = userService.login(LoginRequest.builder()
                .uuid(uuid)
                .build());
        assertThat(response).isNotNull();
        TokenResponse tokenResponse = userService.recreateToken(RefreshTokenRequest.builder()
                .refreshToken(response.getRefreshToken())
                .build());
        assertThat(tokenResponse.getAccessToken()).isNotNull();
        assertThat(tokenResponse.getRefreshToken()).isNotNull();
    }

    @Test
    void setting() {
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        userService.login(LoginRequest.builder()
                .uuid(uuid)
                .build());
        User user = repository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        userService.setTemperature();
        assertThat(user.isTemperature()).isTrue();
        userService.unsetTemperature();
        assertThat(user.isTemperature()).isFalse();
        userService.setRain();
        assertThat(user.isRain()).isTrue();
        userService.unsetRain();
        assertThat(user.isRain()).isFalse();
        userService.setWind();
        assertThat(user.isWind()).isTrue();
        userService.unsetWind();
        assertThat(user.isWind()).isFalse();
    }

    @Test
    void userLogout() {
        String uuid = UUID.randomUUID().toString();;
        String token = "temp";
        userService.signUp(SignRequest.builder()
                .uuid(uuid)
                .token(token)
                .build());
        LoginResponse response = userService.login(LoginRequest.builder()
                .uuid(uuid)
                .build());
        User user = repository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        userService.userLogout();
        assertThatThrownBy(()->userService.recreateToken(RefreshTokenRequest.builder()
                .refreshToken(response.getRefreshToken())
                .build())).isInstanceOf(InvalidTokenException.class);
    }
}