package com.app.weather.domain.user.repository;

import com.app.weather.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
}
