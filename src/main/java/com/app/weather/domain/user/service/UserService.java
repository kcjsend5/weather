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
import com.app.weather.global.exception.uuid.DuplicateUuidException;
import com.app.weather.global.exception.uuid.InvalidUuidException;
import com.app.weather.global.jwt.JwtToken;
import com.app.weather.global.jwt.JwtTokenProvider;
import com.app.weather.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final JwtTokenProvider provider;

    @Transactional
    public void signUp(SignRequest request){
        if(repository.existsByUuid(request.getUuid())){
            throw new DuplicateUuidException();
        }

        User user = User.builder()
                .uuid(request.getUuid())
                .token(request.getToken())
                .build();

        repository.save(user);
    }

    public LoginResponse login(LoginRequest request){

        if(!repository.existsByUuid(request.getUuid())){
            throw new InvalidUuidException();
        }

        JwtToken token = provider.generateToken(request.getUuid());
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        return new LoginResponse(accessToken,refreshToken);

    }

    public void userLogout(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = repository.findById(userId).orElseThrow(UserNotFoundException::new);
        provider.deleteRefreshToken(user.getUuid());
    }


    public TokenResponse recreateToken(RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();

        if(!provider.validateRefreshToken(refreshToken)){
            throw new InvalidTokenException();
        }

        String uuid = provider.getUuidFromToken(refreshToken);
        JwtToken token = provider.generateToken(uuid);

        return new TokenResponse(token.getAccessToken(),token.getRefreshToken());
    }


}
