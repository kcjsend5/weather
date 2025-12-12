package com.app.weather.domain.measurement.domain;

import com.app.weather.domain.forecast.domain.Forecast;
import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.shortforecast.domain.ShortForecast;
import com.app.weather.domain.weather.domain.Weather;
import com.app.weather.type.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double value;

    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weather_id")
    private Weather weather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forecast_id")
    private Forecast forecast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_forecast_id")
    private ShortForecast shortForecast;

    public void setWeather(Weather weather){
        this.weather = weather;
    }

    public void setForecast(Forecast forecast){
        this.forecast = forecast;
    }

    public void setShortForecast(ShortForecast shortForecast){
        this.shortForecast = shortForecast;
    }
}
