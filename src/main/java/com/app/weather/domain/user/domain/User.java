package com.app.weather.domain.user.domain;

import com.app.weather.domain.region.domain.Region;
import com.app.weather.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String uuid;

    private String token;

    @ColumnDefault("false")
    private boolean temperature;//기온이 일정 이상 혹은 이하일때 알림

    @ColumnDefault("false")
    private boolean rain;//비나 눈이 올때 알림

    @ColumnDefault("false")
    private boolean wind;//바람이 일정 이상일때 알림

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    public void setRegion(Region region){
        this.region = region;
    }

    public void setTemperature(boolean temperature){
        this.temperature = temperature;
    }

    public void setRain(boolean rain){
        this.rain = rain;
    }

    public void setWind(boolean wind){
        this.wind = wind;
    }

}
