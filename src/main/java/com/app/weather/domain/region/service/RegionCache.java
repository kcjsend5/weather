package com.app.weather.domain.region.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = "code")
public class RegionCache {

    @Cacheable(key = "#region")
    public String cacheable(String region) {
        return "";
    }

    @CachePut(key = "#region")
    public String saveMessage(String region, String msg) {
        return msg;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @CacheEvict(allEntries = true)
    public void cacheEvict(){}

}
