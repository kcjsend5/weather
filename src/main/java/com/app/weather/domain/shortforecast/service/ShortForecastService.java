package com.app.weather.domain.shortforecast.service;


import com.app.weather.domain.measurement.domain.Measurement;
import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.region.repository.RegionRepository;
import com.app.weather.domain.shortforecast.domain.ShortForecast;
import com.app.weather.domain.shortforecast.repository.ShortForecastRepository;
import com.app.weather.global.convert.ConvertGPS;
import com.app.weather.global.convert.LatXLngY;
import com.app.weather.global.fcst.Fcst;
import com.app.weather.global.fcst.dto.Item;
import com.app.weather.global.fcst.dto.ItemTuple;
import com.app.weather.type.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShortForecastService {

    private final ShortForecastRepository repository;
    private final RegionRepository regionRepository;
    private final ConvertGPS convertGPS;
    private final Fcst fcst;

    @Scheduled(cron = "0 50 * * * *")
    @Transactional
    public void getShortForecastInfo() {
        List<Region> regionList = regionRepository.findAll();
        RestClient restClient = RestClient.create();
        for (Region region : regionList) {
            List<Item> items = fcst.getApi(region, restClient,"https","apihub.kma.go.kr","/api/typ02/openApi/VilageFcstInfoService_2.0/getUltraSrtFcst");

            Map<ItemTuple,List<Item>> map = items.stream()
                    .collect(Collectors
                            .groupingBy(item -> new ItemTuple(item.getFcstDate(), item.getFcstTime())
                            )
                    );
            for (Map.Entry<ItemTuple, List<Item>> entry : map.entrySet()) {
                ItemTuple itemTuple = entry.getKey();
                List<Item> itemList = entry.getValue();

                int fcstDate = Integer.parseInt(itemTuple.getFcstDate());
                int fcstTime = Integer.parseInt(itemTuple.getFcstTime());

                Optional<ShortForecast> optional = repository.findByRegionAndFcstDateAndFcstTime(region,fcstDate, fcstTime);
                List<Measurement> measurements = itemList.stream().map(i->Measurement.builder()
                                .category(i.getCategory())
                                .value(!i.getFcstValue().replaceAll("[^0-9]", "").isEmpty()
                                        ?Double.parseDouble(i.getFcstValue().replaceAll("[^0-9]", ""))
                                        :0.0)
                                .build())
                        .collect(Collectors.toCollection(ArrayList::new));
                if (optional.isPresent()) {
                    ShortForecast f = optional.get();
                    if (!toMap(measurements).equals(toMap(f.getMeasurements()))){
                        f.setMeasurements(measurements);
                    }
                } else {
                    ShortForecast shortForecast = ShortForecast.builder()
                            .fcstDate(fcstDate)
                            .fcstTime(fcstTime)
                            .measurements(measurements)
                            .build();
                    region.addShortForecast(shortForecast);;
                }
            }
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void deleteShortForecast(){
        int date = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        int time = Integer.parseInt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmm")));
        repository.deleteAllByFcstDateLessThan(date);
        repository.deleteAllByFcstDateAndFcstTimeLessThan(date, time);
    }

    private Map<Category, Double> toMap(List<Measurement> list) {
        return list.stream()
                .collect(Collectors.toMap(
                        Measurement::getCategory,
                        Measurement::getValue
                ));
    }

}
