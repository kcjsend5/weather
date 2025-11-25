package com.app.weather.global.dao;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisDao {//Redis DB 접근 시 사용되는 클래스

    private final RedisTemplate<String,Object> redisTemplate;
    private final ValueOperations<String,Object> values;

    public RedisDao(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.values = redisTemplate.opsForValue();
    }

    //기본 설정
    public void setValues(String key,String data){
        values.set(key, data);
    }

    //TTL 기능 활성화
    public void setValues(String key, String data, Duration time){
        values.set(key,data,time);
    }

    // 데이터 조회
    // RefreshToken 검증 시 사용됨
    public Object getValues(String key){
        return values.get(key);
    }

    // 데이터 삭제
    // 로그아웃 시 RefreshToken을 삭제할 때 사용함
    public void deleteValues(String key){
        redisTemplate.delete(key);
    }

}