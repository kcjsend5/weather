package com.app.weather.domain.weather.domain;

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

    @Builder.Default
    private List<Double> obsrValues = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    public void setRegion(Region region){
        this.region = region;
    }
}
