package com.app.weather.domain.user.controller;

import com.app.weather.domain.user.dto.request.LoginRequest;
import com.app.weather.domain.user.dto.request.RefreshTokenRequest;
import com.app.weather.domain.user.dto.request.SignRequest;
import com.app.weather.domain.user.dto.response.LoginResponse;
import com.app.weather.domain.user.dto.response.TokenResponse;
import com.app.weather.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
