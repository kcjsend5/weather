package com.app.weather.domain.region.repository;

import com.app.weather.domain.region.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region,Long> {

    Optional<Region> findByName(String name);
    boolean existsByName(String name);
    List<Region> findByUpperName(String upperName);
}
