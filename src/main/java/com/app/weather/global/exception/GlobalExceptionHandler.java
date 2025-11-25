package com.app.weather.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e){
        ErrorCode errorCode = e.getErrorCode();
        String code = errorCode.getCode();
        String message = errorCode.getMessage();

        log.error("[CustomException] Error Code: {}, Message: {}", errorCode.getCode(), errorCode.getMessage());

        ErrorResponse errorResponse= new ErrorResponse(code, message);

        return new ResponseEntity<>(errorResponse,errorCode.getHttpStatus());
    }

    //커스텀하지 않는 다른 서버 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e){
        log.error("[Unhandled Exception]  Message: {}", e.getMessage());

        ErrorResponse errorResponse= new ErrorResponse("E999", e.getMessage());

        return new ResponseEntity<>(errorResponse,ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
