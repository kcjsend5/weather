package com.app.weather.global.util;



import com.app.weather.global.exception.token.InvalidTokenException;
import com.app.weather.global.jwt.custom.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    public static String getCurrentUuid(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null||authentication.getName()==null){
            throw new RuntimeException("인증 정보가 없습니다.");
        }
        return authentication.getName();
    }

    public static Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null||authentication.getPrincipal()==null||!authentication.isAuthenticated()){
            throw new InvalidTokenException();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getId();
    }

}
