package com.app.weather.global.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String accessToken = resolveToken((HttpServletRequest) servletRequest);

        //액세스 토큰 유효성 검증
        if(accessToken != null){
            if(jwtTokenProvider.validateToken(accessToken)){
                //토큰이 유효한 경우 Authentication 객체 추출 후 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else{//토큰이 유효하지 않는 경우 필터를 진행하지 않고 오류 메세지 반환
                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401 오류 인증 정보 부족
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public String resolveToken(HttpServletRequest request){//액세스 토큰 검증 및 추출

        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
