package com.app.weather.domain.shortforecast.repository;

import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.shortforecast.domain.ShortForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortForecastRepository extends JpaRepository<ShortForecast,Long> {
    Optional<ShortForecast> findByRegionAndFcstDateAndFcstTime(Region region, int fcstDate, int fcstTime);
    void deleteAllByFcstDateBefore(int date);
    void deleteAllByFcstDateAndFcstTimeBefore(int date,int time);
}
