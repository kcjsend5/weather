package com.app.weather.domain.user.controller;

import com.app.weather.domain.user.dto.request.LoginRequest;
import com.app.weather.domain.user.dto.request.RefreshTokenRequest;
import com.app.weather.domain.user.dto.request.SignRequest;
import com.app.weather.domain.user.dto.response.LoginResponse;
import com.app.weather.domain.user.dto.response.TokenResponse;
import com.app.weather.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @PostMapping("/signUp")
    public ResponseEntity<Void> signUp(@RequestBody SignRequest request){
        service.signUp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> Login(@RequestBody LoginRequest request){
        LoginResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> Logout(){
        service.userLogout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> recreateToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(service.recreateToken(request));
    }

    @PatchMapping("/setRain")
    public ResponseEntity<Void> setRain(){
        service.setRain();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unsetRain")
    public ResponseEntity<Void> unsetRain(){
        service.unsetRain();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/setWind")
    public ResponseEntity<Void> setWind(){
        service.setWind();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unsetWind")
    public ResponseEntity<Void> unsetWind(){
        service.unsetWind();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/setTemperature")
    public ResponseEntity<Void> setTemperature(){
        service.setTemperature();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unsetTemperature")
    public ResponseEntity<Void> unsetTemperature(){
        service.unsetTemperature();
        return ResponseEntity.ok().build();
    }
}
