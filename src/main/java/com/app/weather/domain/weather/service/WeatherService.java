package com.app.weather.domain.weather.service;

import com.app.weather.domain.measurement.domain.Measurement;
import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.domain.user.repository.UserRepositoryImpl;
import com.app.weather.domain.weather.domain.Weather;
import com.app.weather.domain.weather.dto.WeatherResponse;
import com.app.weather.domain.weather.repository.WeatherRepository;
import com.app.weather.global.convert.ConvertGPS;
import com.app.weather.global.convert.LatXLngY;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.exception.weather.WeatherNotFoundException;
import com.app.weather.global.kafka.producer.EventProducerService;
import com.app.weather.global.util.SecurityUtil;
import com.app.weather.type.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    private final WeatherRepository repository;
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final ConvertGPS convertGPS;
    private final EventProducerService producerService;
    private final StringRedisTemplate redisTemplate;

    @Value("${weather.key}")
    private String authKey;

    private Map<Integer, String> ptyMap = Map.of(
            0, "없음", 1, "비", 2, "비/눈",
            3, "눈", 4, "빗방울", 5, "빗방울눈날림", 6, "눈날림"
    );

    @Scheduled(cron = "0 11 * * * *")
    @Transactional
    public void getWeatherInfo() {
        List<Region> regionList = regionRepository.findAll();
        RestClient restClient = RestClient.create();
        for (Region region : regionList) {
            LatXLngY xy = convertGPS.convertGRID_GPS(region.getLat(), region.getLon());
            ResponseEntity<WeatherResponse> response = restClient.get()
                    .uri(uriBuilder->uriBuilder
                            .scheme("https")
                            .host("apihub.kma.go.kr")
                            .path("/api/typ02/openApi/VilageFcstInfoService_2.0/getUltraSrtNcst")
                            .queryParam("authKey",authKey)
                            .queryParam("base_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                            .queryParam("base_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")))
                            .queryParam("nx", (int)xy.x)
                            .queryParam("ny", (int)xy.y)
                            .queryParam("dataType", "JSON")
                            .build())
                    .retrieve()
                    .toEntity(WeatherResponse.class);
            WeatherResponse body = response.getBody();
            List<Measurement> measurements = body.getResponse().getBody().getItems().getItem().stream().map(i-> Measurement.builder()
                        .value(Double.valueOf(i.getObsrValue()))
                        .category(i.getCategory())
                        .build())
                    .toList();
            Map<Category,Double> map = measurements.stream()
                    .collect(Collectors.toMap(
                            Measurement::getCategory,
                            Measurement::getValue
                    ));
            Double t = map.get(Category.T1H);
            Double p = map.get(Category.RN1);
            Double w = map.get(Category.WSD);
            Double r = map.get(Category.REH);

            Weather weather = Weather.builder()
                    .baseDate(body.getResponse().getBody().getItems().getItem().getFirst().getBaseDate())
                    .baseTime(body.getResponse().getBody().getItems().getItem().getFirst().getBaseTime())
                    .score(createScore(t,p , w, r))
                    .build();
            for(Measurement m : measurements){
                weather.addMeasurement(m);
            }
            region.addWeather(weather);

        }
    }

    //캐시를 사용하여 각 유저가 만약 알림을 받았고 해당 알림이 온 후 날씨가 설정한 수준 이하로 안내려가면 다시 보내지 않도록
    @Scheduled(cron = "0 15 * * * *")
    public void sendAlarm(){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        List<Region> regions = regionRepository.findAll();
        for(Region region:regions){
            Weather weather = repository.findFirstByRegionOrderByCreatedAtDesc(region).orElseThrow(WeatherNotFoundException::new);
            List<Measurement> measurements = weather.getMeasurements();
            Map<Category,Double> map = measurements.stream()
                    .collect(Collectors.toMap(
                            Measurement::getCategory,
                            Measurement::getValue
                    ));
            double temperature = map.getOrDefault(Category.T1H, 0.0);
            double wind = map.getOrDefault(Category.WSD, 0.0);
            double rain = map.getOrDefault(Category.PTY, 0.0);
            double precipitation = map.getOrDefault(Category.RN1, 0.0);
            //querydsl을 사용하여 동적 쿼리 사용하기 예) 입력된 기온이 32도 이상 혹은 -5도 이하인 경우 user의 temperature필드가 true인 유저를 찾아라
            List<User> users = userRepository.searchAlarm(temperature, wind, rain);
            for(User user: users){
                String uuid = user.getUuid();
                if(user.isTemperature()&&(temperature >= 33.0 ||temperature <= -5.0)){
                    if(Boolean.TRUE.equals(ops.setIfAbsent(uuid+":Temperature","true", Duration.ofHours(24)))){
                        sendTemperature(user, temperature);
                    }
                }
                if(rain != 0.0 && user.isRain()) {
                    if (Boolean.TRUE.equals(ops.setIfAbsent(uuid + ":Rain", "true", Duration.ofHours(24)))) {
                        sendRain(user, rain, precipitation);
                    }
                }
                if(user.isWind()&&wind >= 8.0) {
                    if (Boolean.TRUE.equals(ops.setIfAbsent(uuid + ":Wind", "true", Duration.ofHours(24)))) {
                        sendWind(user, wind);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 13 * * * *")
    public void resetAlarm(){
        List<Region> regions = regionRepository.findAll();
        for(Region region:regions){
            List<User> users = userRepository.searchRegionAlarm(region);
            Weather weather = repository.findFirstByRegionOrderByCreatedAtDesc(region).orElseThrow(WeatherNotFoundException::new);
            for(User user:users){
                List<Measurement> measurements = weather.getMeasurements();
                Map<Category,Double> map = measurements.stream()
                        .collect(Collectors.toMap(
                                Measurement::getCategory,
                                Measurement::getValue
                        ));
                double temperature = map.getOrDefault(Category.T1H, 0.0);
                double wind = map.getOrDefault(Category.WSD, 0.0);
                double rain = map.getOrDefault(Category.PTY, 0.0);
                String uuid = user.getUuid();
                if(user.isTemperature()&&(temperature < 33.0&&temperature > -5.0)){
                    redisTemplate.delete(uuid+":Temperature");
                }
                if((user.isRain()&&(rain == 0.0))){
                    redisTemplate.delete(uuid+":Rain");
                }
                if(user.isWind()&&wind < 8.0){
                    redisTemplate.delete(uuid+":Wind");
                }
            }
        }
    }

    @Scheduled(cron = "0 16 6,18 * * *")
    public void sendWeather(){
        List<Region> regionList = regionRepository.findAll();
        for(Region region:regionList){
            Weather weather = repository.findFirstByRegionOrderByCreatedAtDesc(region).orElseThrow(WeatherNotFoundException::new);
            List<Measurement> measurements = weather.getMeasurements();
            Map<Category,Double> map = measurements.stream().collect(Collectors.toMap(Measurement::getCategory, Measurement::getValue));
            double ptyValue = map.getOrDefault(Category.PTY,0.0);
            double t1hValue = map.getOrDefault(Category.T1H,0.0);
            double rehValue = map.getOrDefault(Category.REH,0.0);
            String p = ptyMap.getOrDefault((int) ptyValue, "없음");

            String message = String.format("강수형태: %s | 기온: %.1f℃ 습도: %.1f%%",p,t1hValue,rehValue);
            producerService.sendMessage(region.getName(), message);
        }
    }

    @Transactional
    @Scheduled(cron = "0 20 * * * *")
    public void deleteWeather(){
        repository.deleteAllByCreatedAtBefore(LocalDateTime.now().minusHours(24));
    }

    private double createScore(double t,double p,double w,double r){
        double score = 0;
        if(18.0<=t&&t<=25.0){
            score += 25.0;
        } else if (10.0<=t&&t<18.0) {
            score += 25.0*(t-10.0)/8.0;
        } else if (25.0<t&&t<=32.0) {
            score += 25.0*(32.0-t)/7.0;
        }
        if(p==0.0){
            score += 25.0;
        } else if (p<=2.0) {
            score += 20.0;
        } else if (p<=10.0) {
            score += 25.0-(15*((p-2)/8));
        } else if (p<=50.0) {
            score += 5.0*(50.0-p)/(40.0);
        }
        if(w<3.0){
            score+=25.0;
        } else if (w<=8.0) {
            score+=25.0-((w-3.0)/5.0)*(15.0);
        } else if (w<=15) {
            score+=10.0-((w-8.0)/7.0)*10.0;
        }
        if(30.0<=r&&r<=60.0){
            score += 25.0;
        } else if (10.0<=r&&r<30.0) {
            score += 25.0*(r-10.0)/20.0;
        } else if (60.0<r&&r<=95.0) {
            score += 25.0 * (95.0 - r) / 35.0;
        }

        return Math.round(score*100)/100.0;
    }

    private void sendTemperature(User user,double temperature){
        StringBuilder m = new StringBuilder();
        if(temperature >= 33.0){
            m.append("폭염주의");
        }else {
            m.append("한파주의");
        }
        m.append("|");
        m.append("현재 기온:").append(temperature).append("℃");
        producerService.sendAlarm(user.getUuid(),m.toString());
    }
    private void sendWind(User user,double wind){
        StringBuilder m = new StringBuilder();
        m.append("강풍주의").append("|").append("현재 풍속:").append(wind).append("m/s");
        producerService.sendAlarm(user.getUuid(),m.toString());

    }
    private void sendRain(User user,double rain,double precipitation){
        StringBuilder m = new StringBuilder();
        m.append(ptyMap.get((int) rain)).append("주의").append("|").append("현재 강수량:").append(precipitation).append("mm");
        producerService.sendAlarm(user.getUuid(),m.toString());
    }
}
