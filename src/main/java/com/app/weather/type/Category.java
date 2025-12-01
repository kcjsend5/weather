package com.app.weather.type;

public enum Category {
    POP("강수 확률"),
    PTY("강수 형태"),
    PCP("1시간 강수량"),
    REH("습도"),
    SNO("1시간 신적설"),
    SKY("하늘상태"),
    TMP("1시간 기온"),
    TMN("일 최저기온"),
    TMX("일 최고기온"),
    UUU("풍속(동서성분)"),
    VVV("풍속(남북성분)"),
    WAV("파고"),
    VEC("풍향"),
    WSD("풍속"),
    T1H("기온"),
    RN1("1시간 강수량(초단기)"),
    LGT("낙뢰");

    private final String name;

    Category(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
