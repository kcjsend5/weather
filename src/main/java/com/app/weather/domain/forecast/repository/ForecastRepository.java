package com.app.weather.domain.forecast.repository;

import com.app.weather.domain.forecast.domain.Forecast;
import com.app.weather.domain.region.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ForecastRepository extends JpaRepository<Forecast,Long> {
    Optional<Forecast> findByRegionAndFcstDateAndFcstTime(Region region, int fcstDate, int fcstTime);
    void deleteAllByFcstDateBefore(int date);
    void deleteAllByFcstDateAndFcstTimeBefore(int date,int time);
}
