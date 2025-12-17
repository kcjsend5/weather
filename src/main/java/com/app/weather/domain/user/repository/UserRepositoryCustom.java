package com.app.weather.domain.user.repository;

import com.app.weather.domain.user.domain.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> searchAlarm(Double temperature,Double wind,Double rain);
}
