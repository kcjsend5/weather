package com.app.weather.global.config;


import com.app.weather.global.jwt.JwtAccessDeniedHandler;
import com.app.weather.global.jwt.JwtAuthenticationEntryPoint;
import com.app.weather.global.jwt.JwtAuthenticationFilter;
import com.app.weather.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private static final String[] list ={
            "/user/login",
            "/user/signup"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        //RestApi -> httpBasic,csrf 보안 사용하지 않음.
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)//사이트 간 위조 요청을 막기 위한 보안정책, Jwt 토큰을 사용함으로 필요없음
                .cors(AbstractHttpConfigurer::disable);
                //.cors(cors -> cors.configurationSource(corsConfigurationSource())); 실제 프론트엔드와 통신 시 포트 3000으로 맞추고 주석 해제
                //교차 출처 리소스 공유 정책 제어, 특정 출처(포트,프로토콜,도메인) 제외 차단

        //Jwt 방식임으로 세션 사용 안함
        //세션 생성 정책: ALWAYS, NEVER, IF_REQUIRED, STATELESS
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //Jwt 방식임으로 폼 로그인,로그아웃을 사용하지 않음
        httpSecurity.formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        //http 리퀘스트 인증 설정
        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(list).permitAll()
                .anyRequest().permitAll());
                //.anyRequest().authenticated()); //실제 운영 중 변경

        //커스텀 필터 UsernamePasswordAuthenticationFilter 이전 실행
        httpSecurity.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        httpSecurity.exceptionHandling(
                e->{
                   e.accessDeniedHandler(jwtAccessDeniedHandler);
                   e.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                });

        return httpSecurity.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        // BCrypt 알고리즘만 사용해서 접두어 없이 순수한 해시값만 저장됨
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


}
