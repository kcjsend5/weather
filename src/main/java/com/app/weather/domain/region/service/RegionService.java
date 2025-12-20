package com.app.weather.domain.region.service;

import com.app.weather.domain.forecast.domain.Forecast;
import com.app.weather.domain.measurement.domain.Measurement;
import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.region.dto.request.LocationRequest;
import com.app.weather.domain.region.dto.response.*;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.shortforecast.domain.ShortForecast;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.user.repository.UserRepository;
import com.app.weather.domain.weather.domain.Weather;
import com.app.weather.domain.weather.repository.WeatherRepository;
import com.app.weather.global.exception.region.RegionNotFoundException;
import com.app.weather.global.exception.user.UserNotFoundException;
import com.app.weather.global.exception.weather.WeatherNotFoundException;
import com.app.weather.global.fcm.FcmService;
import com.app.weather.global.kafka.producer.EventProducerService;
import com.app.weather.global.util.SecurityUtil;
import com.app.weather.type.Category;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@CacheConfig(cacheNames = "code")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

    private final RegionRepository repository;
    private final UserRepository userRepository;
    private final EventProducerService producerService;
    private final WeatherRepository weatherRepository;
    @Value("${geo.access}")
    private String accessKey;
    @Value("${geo.secret}")
    private String secretKey;
    @Value("${weather.key}")
    private String authKey;

    // 메소드: 지역 특보 실시간 알림

    public RegionResponse getRegionInfo(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();

        return RegionResponse.builder()
                .regionName(region.getName())
                .lat(region.getLat())
                .lon(region.getLon())
                .build();
    }

    public WeatherResponse getNowWeather(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        Weather weather = weatherRepository.findFirstByRegionOrderByCreatedAtDesc(region).orElseThrow(WeatherNotFoundException::new);
        List<MeasurementResponse> measurements = weather
                .getMeasurements()
                .stream()
                .map(m->MeasurementResponse.builder()
                        .value(m.getValue())
                        .category(m.getCategory())
                        .build())
                .toList();
        return WeatherResponse.builder()
                .baseDate(weather.getBaseDate())
                .baseTime(weather.getBaseTime())
                .measurements(measurements)
                .build();
    }

    public List<WeatherResponse> getPastWeather(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        List<Weather> weathers = region.getWeathers();

        return weathers.stream().map(w->WeatherResponse.builder()
                    .baseTime(w.getBaseTime())
                    .baseDate(w.getBaseDate())
                    .measurements(w.getMeasurements().stream().map(m->MeasurementResponse.builder()
                                    .value(m.getValue())
                                    .category(m.getCategory())
                                    .build())
                            .toList())
                    .build()
        ).toList();
    }

    public List<ShortForecastResponse> getShortForecastInfo(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        List<ShortForecast> shortForecast = region.getShortForecasts();
        return shortForecast.stream()
                .map(s->ShortForecastResponse.builder()
                        .fcstDate(s.getFcstDate())
                        .fcstTime(s.getFcstTime())
                        .measurements(s.getMeasurements().stream().map(m->MeasurementResponse.builder()
                                    .value(m.getValue())
                                    .category(m.getCategory())
                                    .build())
                                .toList())
                        .build()
                ).toList();
    }

    public List<ForecastResponse> getForecastInfo(){
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Region region = user.getRegion();
        List<Forecast> Forecast = region.getForecasts();

        return Forecast.stream()
                .map(s->ForecastResponse.builder()
                        .fcstDate(s.getFcstDate())
                        .fcstTime(s.getFcstTime())
                        .measurements(s.getMeasurements().stream().map(m->MeasurementResponse.builder()
                                        .value(m.getValue())
                                        .category(m.getCategory())
                                        .build())
                                .toList())
                        .build()
                ).toList();
    }

    //사용자 위치에 따른 지역 변경
    @Transactional
    public void updateLocation(LocationRequest request) throws NoSuchAlgorithmException, InvalidKeyException {

        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if(user.getRegion()==null){
            String locName = getLocation(request);
            Region region = repository.findByName(locName).orElseThrow(RegionNotFoundException::new);
            region.addUser(user);
        } else{
            double distance = 4000.0;
            if(distance <= getDistance(request.getUserLat(), request.getUserLon(),user.getRegion().getLat(),user.getRegion().getLon())){
                String locName = getLocation(request);
                user.getRegion().getUsers().remove(user);
                Region region = repository.findByName(locName).orElseThrow(RegionNotFoundException::new);
                region.addUser(user);
            }
        }
    }

    @Scheduled(cron = "0 0 0/2 * * *")
    public void specialReport(){
        RestClient restClient = RestClient.create();
        ResponseEntity<String> reponse = restClient
                .get()
                .uri(uriBuilder-> uriBuilder.path("https://apihub.kma.go.kr/api/typ01/url/wrn_now_data_new.php")
                        .queryParam("fe", "f")
                        .queryParam("authKey", authKey)
                        .build())
                .retrieve()
                .toEntity(String.class);
        String result = reponse.getBody();
        String[] list = result.split("\n");
        for(int i = 19; i<list.length; i++){
            String[] s = list[i].split(",");
            String name = s[3].trim();
            String time = s[4].trim();
            String report = s[6].trim();
            String level = s[7].trim();
            String cmd = s[8].trim();
            String end = s[9];

            List<Region> regions = repository.findByUpperNameOrAreaName(name,name);
            String t = cmd.equals("예비")?"특보":"보";
            String m = " "+report+level+t+" "+cmd+"|"+"발표 시각: "+time+" "+"해제예고 시점: "+end;
            if(cacheable(name).equals(m)){
                continue;
            }
            saveMessage(name, m);
            for(Region region:regions){
                String message = region.getName()+m;
                producerService.sendMessage(region.getName(), message);
            }

        }
    }

    @Cacheable(key = "#region")
    public String cacheable(String region){
        return null;
    }

    @CachePut(key = "#region")
    public String saveMessage(String region, String msg) {
        return msg;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @CacheEvict(allEntries = true)
    public void cacheEvict(){}
    
    private String getLocation(LocationRequest request) throws NoSuchAlgorithmException, InvalidKeyException {
        RestClient restClient = RestClient.create();
        List<String> names = new ArrayList<>(List.of("coords","output","orders"));
        List<String> params = new ArrayList<>(List.of(request.getUserLat() + "," + request.getUserLon(),"json","admcode"));
        URI uri = createUri("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc",names,params);
        ResponseEntity<GeoResponse> response = restClient
                .get()
                .uri(uri)
                .header("x-ncp-apigw-signature-v2",makeSignature("GET", uri.toString()))
                .header("x-ncp-apigw-timestamp", String.valueOf(System.currentTimeMillis()))
                .header("x-ncp-iam-access-key", accessKey)
                .retrieve()
                .toEntity(GeoResponse.class);
        GeoResponse geo = response.getBody();
        String locName = geo.getStatus().getResults().getFirst().getRegion().getArea3().getName();
        String upperName = geo.getStatus().getResults().getFirst().getRegion().getArea2().getName();
        String areaName = geo.getStatus().getResults().getFirst().getRegion().getArea1().getName();
        if(!repository.existsByName(locName)) {
            //x가 경도 y가 위도
            Float x = geo.getStatus().getResults().getFirst().getRegion().getArea3().getCoords().getCenter().getX();
            Float y = geo.getStatus().getResults().getFirst().getRegion().getArea3().getCoords().getCenter().getY();
            repository.save(Region.builder()
                    .areaName(areaName)
                    .upperName(upperName)
                    .name(locName)
                    .lat(y)
                    .lon(x)
                    .build());
        }
        return locName;
    }

    //하버사인 공식
    private double getDistance(double lat1, double lon1,double lat2, double lon2){
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)* Math.sin(dLat/2)+ Math.cos(Math.toRadians(lat1))* Math.cos(Math.toRadians(lat2))* Math.sin(dLon/2)* Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double earthDistance = 6371.0;
        double d = earthDistance * c * 1000;    // Distance in m
        return d;

    }

    private URI createUri(String uri,List<String> names,List<String> params){
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);
        for(int i = 0; i<names.size();i++){builder.queryParam(names.get(i),params.get(i));}
        return builder.build().toUri();
    }

    //네이버 클라우드 api 시그니처 키 생성 공통 요청 헤더 x-ncp-apigw-signature-v2 필드에 들어가는 값
    private String makeSignature(String method,String url) throws InvalidKeyException, NoSuchAlgorithmException {
        String space = " ";					// one space
        String newLine = "\n";					// new line
        String timestamp = String.valueOf(System.currentTimeMillis());// current timestamp (epoch)

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeBase64String(rawHmac);
    }


}
