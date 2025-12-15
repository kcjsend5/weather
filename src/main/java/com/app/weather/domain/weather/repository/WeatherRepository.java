package com.app.weather.domain.weather.repository;

import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.weather.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather,Long> {
    void deleteAllByCreatedAtBefore(LocalDateTime dateTime);
    Optional<Weather> findFirstByRegionOrderByCreateAtDesc(Region region);
}
