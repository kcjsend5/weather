package com.app.weather.domain.weather.domain;

import com.app.weather.domain.measurement.domain.Measurement;
import com.app.weather.domain.region.domain.Region;
import com.app.weather.global.entity.BaseEntity;
import com.app.weather.type.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Weather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baseDate;

    private String baseTime;

    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(mappedBy = "weather",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Measurement> measurements = new ArrayList<>();


    public void setRegion(Region region){
        this.region = region;
    }

    public void addMeasurement(Measurement measurement){
        this.measurements.add(measurement);
        measurement.setWeather(this);
    }

    public void setMeasurements(List<Measurement> measurements){
        this.measurements.clear();
        for(Measurement m:measurements){
            addMeasurement(m);
        }
    }

}
