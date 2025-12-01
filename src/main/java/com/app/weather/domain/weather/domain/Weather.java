package com.app.weather.domain.weather.domain;

import com.app.weather.domain.region.domain.Region;
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
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int baseDate;

    private int baseTime;

    @Builder.Default
    private List<Integer> obsrValues = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;
}
