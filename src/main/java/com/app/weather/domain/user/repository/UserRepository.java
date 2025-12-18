package com.app.weather.domain.user.repository;

import com.app.weather.domain.region.domain.Region;
import com.app.weather.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>,UserRepositoryCustom {
    Optional<User> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
}
