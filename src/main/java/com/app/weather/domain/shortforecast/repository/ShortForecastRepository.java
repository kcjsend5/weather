package com.app.weather.domain.shortforecast.repository;

import com.app.weather.domain.shortforecast.domain.ShortForecast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortForecastRepository extends JpaRepository<ShortForecast,Long> {
}
