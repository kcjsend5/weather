package com.app.weather.domain.user.controller;

import com.app.weather.domain.user.dto.request.LoginRequest;
import com.app.weather.domain.user.dto.request.RefreshTokenRequest;
import com.app.weather.domain.user.dto.request.SignRequest;
import com.app.weather.domain.user.dto.response.LoginResponse;
import com.app.weather.domain.user.dto.response.TokenResponse;
import com.app.weather.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @Tag(name = "인증",description = "회원가입 및 로그인,로그아웃")
    @Operation(summary = "회원가입",description = "날씨앱 설치시 자동으로 호출하는 유저 등록 API", security = @SecurityRequirement(name = ""))
    @Parameters({
            @Parameter(name = "uuid",description = "고유 식별자 번호",example = "fba44015-fefc-477a-9481-f194038222be"),
            @Parameter(name = "token",description = "FCM토큰")
    })
    @PostMapping("/signUp")
    public ResponseEntity<Void> signUp(@RequestBody SignRequest request){
        service.signUp(request);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "인증",description = "회원가입 및 로그인,로그아웃")
    @Operation(summary = "로그인", description = "날씨앱 사용 시 자동으로 호출하는 유저 로그인 API",security = @SecurityRequirement(name = ""))
    @Parameter(name = "uuid",description = "고유 식별자 번호",example = "fba44015-fefc-477a-9481-f194038222be")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> Login(@RequestBody LoginRequest request){
        LoginResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @Tag(name = "인증",description = "회원가입 및 로그인,로그아웃")
    @Operation(summary = "로그아웃",description = "유저 로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<Void> Logout(){
        service.userLogout();
        return ResponseEntity.ok().build();
    }
    @Tag(name = "인증",description = "회원가입 및 로그인,로그아웃")
    @Operation(summary = "토큰 재발급",description = "refreshToken을 이용한 accessToken 재발급")
    @Parameter(name = "refreshToken",description = "JWT 재발급 토큰")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> recreateToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(service.recreateToken(request));
    }

    @Tag(name = "알림 설정",description = "날씨가 특정 임계점 이상인 경우 알림이 오도록 하는 설정")
    @Operation(summary = "강우 알림",description = "강우시 알림을 받도록 하는 API")
    @PatchMapping("/setRain")
    public ResponseEntity<Void> setRain(){
        service.setRain();
        return ResponseEntity.ok().build();
    }

    @Tag(name = "알림 설정",description = "날씨가 특정 임계점 이상인 경우 알림이 오도록 하는 설정")
    @Operation(summary = "강우 알림해제",description = "강우 알림을 해제 하는 API")
    @PatchMapping("/unsetRain")
    public ResponseEntity<Void> unsetRain(){
        service.unsetRain();
        return ResponseEntity.ok().build();
    }

    @Tag(name = "알림 설정",description = "날씨가 특정 임계점 이상인 경우 알림이 오도록 하는 설정")
    @Operation(summary = "강풍 알림",description = "강풍시 알림을 받도록 하는 API")
    @PatchMapping("/setWind")
    public ResponseEntity<Void> setWind(){
        service.setWind();
        return ResponseEntity.ok().build();
    }

    @Tag(name = "알림 설정",description = "날씨가 특정 임계점 이상인 경우 알림이 오도록 하는 설정")
    @Operation(summary = "강풍 알림해제",description = "강풍 알림을 해제 하는 API")
    @PatchMapping("/unsetWind")
    public ResponseEntity<Void> unsetWind(){
        service.unsetWind();
        return ResponseEntity.ok().build();
    }

    @Tag(name = "알림 설정",description = "날씨가 특정 임계점 이상인 경우 알림이 오도록 하는 설정")
    @Operation(summary = "기온 알림",description = "기온이 33도 이상 혹은 -5도 이하인 경우 알림을 받도록 하는 API")
    @PatchMapping("/setTemperature")
    public ResponseEntity<Void> setTemperature(){
        service.setTemperature();
        return ResponseEntity.ok().build();
    }

    @Tag(name = "알림 설정",description = "날씨가 특정 임계점 이상인 경우 알림이 오도록 하는 설정")
    @Operation(summary = "기온 알림해제",description = "기온 알림을 해제 하는 API")
    @PatchMapping("/unsetTemperature")
    public ResponseEntity<Void> unsetTemperature(){
        service.unsetTemperature();
        return ResponseEntity.ok().build();
    }
}
