package com.app.weather.global.jwt;

import com.app.weather.domain.user.domain.User;
import com.app.weather.global.dao.RedisDao;
import com.app.weather.global.exception.user.UserNotFoundException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String GRANT_TYPE = "Bearer";

    private final Key key;
    private final UserDetailsService userDetailsService;
    private final RedisDao redisDao;

    @Value("${jwt.expiration.accessToken}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.expiration.refreshToken}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserDetailsService userDetailsService, RedisDao redisDao){
        this.userDetailsService = userDetailsService;
        this.redisDao = redisDao;
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String uuid, Date expireDate){
        return Jwts.builder()
                .setSubject(uuid)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String uuid,Date exprieDate){
        return Jwts.builder()
                .setSubject(uuid)
                .setExpiration(exprieDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtToken generateToken(String uuid){

        long now = (new Date()).getTime();


        Date accessTokenExpire = new Date(now+ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = generateAccessToken(uuid,accessTokenExpire);

        Date refreshTokenExpire = new Date(now+REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = generateRefreshToken(uuid, refreshTokenExpire);

        redisDao.setValues(uuid,refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));

        return JwtToken.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보 꺼내기
    public Authentication getAuthentication(String accessToken) {
        // JWT 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 클래스
        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(principal, "");
    }

    private Claims parseClaims(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)//암호화한 키로 다시 복호화
                    .build()
                        .parseClaimsJws(token)//토큰 검증, 검증 후 파싱: Jwts 토큰을 Header, Body, Signature 세 부분으로 분리
                    .getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public String getUuidFromToken(String token){

        try {
            Claims claims = parseClaims(token);
            return claims.getSubject();
        } catch (ExpiredJwtException e){// 토큰이 만료되어도 클레임 내용을 가져올 수 있음
            return e.getClaims().getSubject();
        }
    }

    //토큰 정보 검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)//암호화한 키로 다시 복호화
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e){
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token",e);
        } catch (IllegalArgumentException e){
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    public boolean validateRefreshToken(String token){
        if(!validateToken(token)){
            return false;
        }

        try{
            String uuid = getUuidFromToken(token);
            String redisToken = (String)redisDao.getValues(uuid);
            return token.equals(redisToken);
        } catch (Exception e){
            log.info("RefreshToken Validation Failed", e);
            return false;
        }
    }

    public void deleteRefreshToken(String uuid){
        if(uuid == null||uuid.isBlank()){
            throw new UserNotFoundException();
        }

        redisDao.deleteValues(uuid);
    }

}
