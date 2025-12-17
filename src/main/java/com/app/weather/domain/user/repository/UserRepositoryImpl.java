package com.app.weather.domain.user.repository;

import com.app.weather.domain.user.domain.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import static com.app.weather.domain.user.domain.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> searchAlarm(Double temperature, Double wind, Double rain) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        BooleanExpression tempExpr = getTemperature(temperature);
        if(tempExpr != null)
            booleanBuilder.or(tempExpr);
        BooleanExpression windExpr = getWind(wind);
        if(windExpr != null)
            booleanBuilder.or(windExpr);
        BooleanExpression rainExpr = getRain(rain);
        if(rainExpr != null)
            booleanBuilder.or(rainExpr);

        if(!booleanBuilder.hasValue()){
            return List.of();
        }

        return queryFactory
                .select(user)
                .from(user)
                .where(booleanBuilder)
                .fetch();
    }

    private BooleanExpression getTemperature(Double temperature) {
        if(temperature == null) return null;
        return temperature >= 33.0 || temperature <= -5.0 ? user.temperature.isTrue() : null;
    }

    private BooleanExpression getWind(Double wind){
        if(wind == null) return null;
        return wind >= 8.0 ? user.wind.isTrue():null;
    }

    private BooleanExpression getRain(Double rain){
        if(rain == null) return null;
        return rain != 0.0 ? user.rain.isTrue():null;
    }
}
