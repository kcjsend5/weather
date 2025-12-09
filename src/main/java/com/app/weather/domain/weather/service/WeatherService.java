package com.app.weather.domain.weather.service;

import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.weather.domain.Weather;
import com.app.weather.domain.weather.dto.WeatherResponse;
import com.app.weather.domain.weather.repository.WeatherRepository;
import com.app.weather.global.convert.ConvertGPS;
import com.app.weather.global.convert.LatXLngY;
import com.app.weather.global.exception.weather.WeatherNotFoundException;
import com.app.weather.global.kafka.producer.EventProducerService;
import com.app.weather.type.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {

    private final WeatherRepository repository;
    private final RegionRepository regionRepository;
    private final ConvertGPS convertGPS;
    private final EventProducerService producerService;

    @Scheduled(cron = "0 11 * * *")
    @Transactional
    public void getWeatherInfo() {
        List<Region> regionList = regionRepository.findAll();
        RestClient restClient = RestClient.create();
        for (Region region : regionList) {
            LatXLngY xy = convertGPS.convertGRID_GPS(region.getLat(), region.getLon());
            ResponseEntity<WeatherResponse> response = restClient.get()
                    .uri(uriBuilder->uriBuilder.path("https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getUltraSrtNcst")
                            .queryParam("authKey")
                            .queryParam("base_date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                            .queryParam("base_time", LocalDate.now().format(DateTimeFormatter.ofPattern("HHmm")))
                            .queryParam("nx", xy.x)
                            .queryParam("ny", xy.y)
                            .queryParam("dataType", "JSON")
                            .build())
                    .retrieve()
                    .toEntity(WeatherResponse.class);
            WeatherResponse body = response.getBody();

            Weather weather = Weather.builder()
                    .baseDate(body.getResponse().getBody().getItems().getItem().getFirst().getBaseDate())
                    .baseTime(body.getResponse().getBody().getItems().getItem().getFirst().getBaseTime())
                    .categories(body.getResponse().getBody().getItems().getItem().stream().map(WeatherResponse.Response.Body.Items.Item::getCategory).toList())
                    .obsrValues(body.getResponse().getBody().getItems().getItem().stream().map(i -> Double.valueOf(i.getObsrValue())).toList())
                    .build();
            region.addWeather(weather);
        }
    }

    @Scheduled(cron = "0 15 6,18 * *")
    public void sendWeather(){
        Map<Integer, String> ptyMap = Map.of(
                0, "없음", 1, "비", 2, "비/눈",
                3, "눈", 4, "빗방울", 5, "빗방울눈날림", 6, "눈날림"
        );
        List<Region> regionList = regionRepository.findAll();
        for(Region region:regionList){
            Weather weather = region.getWeathers().getLast();
            List<Category> categories = weather.getCategories();
            int pty = categories.indexOf(Category.PTY);
            int t1h = categories.indexOf(Category.T1H);
            int reh = categories.indexOf(Category.REH);
            List<Double> obsrValues = weather.getObsrValues();
            double ptyValue = obsrValues.get(pty);
            double t1hValue = obsrValues.get(t1h);
            double rehValue = obsrValues.get(reh);
            String p = ptyMap.getOrDefault((int) ptyValue, "없음");

            String message = String.format("강수형태: %s, 기온: %.1f℃ 습도: %.1f%",p,t1hValue,rehValue);
            producerService.sendMessage(region.getName(), message);
        }
    }

    @Scheduled(cron = "0 20 * * *")
    public void deleteWeather(){
        repository.deleteAllByCreatedAtBefore(LocalDateTime.now().minusHours(24));
    }
}
