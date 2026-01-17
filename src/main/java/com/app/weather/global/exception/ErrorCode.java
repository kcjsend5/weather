package com.app.weather.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ECO001", "서버 오류가 발생했습니다."),
    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EU001", "사용자를 찾을 수 없습니다."),
    FORBIDDEN_USER(HttpStatus.FORBIDDEN,"EU002","인가되지 않은 유저입니다"),
    // uuid
    INVALID_UUID(HttpStatus.UNAUTHORIZED,"EE001","존재하지 않는 uuid입니다"),
    DUPLICATE_UUID(HttpStatus.CONFLICT,"EE002","이미 존재하는 uuid입니다"),
    // token
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"ET001","유효하지 않는 토큰입니다"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "ET002", "토큰을 찾을 수 없습니다."),
    //category
    INVALID_CATEGORY(HttpStatus.UNAUTHORIZED,"EC001","존재하지 않는 카테고리입니다"),
    //uri
    INVALID_URI(HttpStatus.UNAUTHORIZED,"EUR001","존재하지 않는 주소입니다"),
    //delivery
    DELIVERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"ED001","전송에 실패하였습니다"),
    //region
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "ER001", "지역을 찾을 수 없습니다."),
    //weather
    WEATHER_NOT_FOUND(HttpStatus.NOT_FOUND, "EW001", "현재 날씨를 찾을 수 없습니다."),
    //location
    LOCATION_CRYPTO(HttpStatus.INTERNAL_SERVER_ERROR, "EL001", "위치 정보 처리 중 오류가 발생하였습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
