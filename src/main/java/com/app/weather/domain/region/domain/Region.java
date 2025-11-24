package com.app.weather.domain.region.domain;

import com.app.weather.domain.forecast.domain.Forecast;
import com.app.weather.domain.shortforecast.domain.ShortForecast;
import com.app.weather.domain.user.domain.User;
import com.app.weather.domain.weather.domain.Weather;
import com.app.weather.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int nx;

    private int ny;

    @OneToMany(mappedBy = "region",cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "region",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Forecast> forecasts = new ArrayList<>();

    @OneToMany(mappedBy = "region",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<ShortForecast> shortForecasts = new ArrayList<>();

    @OneToMany(mappedBy = "region",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Weather> weathers = new ArrayList<>();
}
