package com.lshdainty.porest.common.type;

public enum CountryCode implements DisplayType {
    KR("KOR","410","Asia","아시아","아시아","Korea","대한민국"),
    US("USA","840","America","북아메리카","미주","United States of America","미합중국"),
    JP("JPN","392","Asia","아시아","아주","Japan","일본"),
    CN("CHN","156","Asia","아시아","아주","China","중국"),
    VN("VNM","704","Asia","아시아","아주","Vietnam","베트남"),
    MY("MYS","458","Asia","아시아","아주","Malaysia","말레이시아"),
    PL("POL","616","Europe","유럽","유럽","Poland","폴란드");

    private String alpha3;
    private String numeric;
    private String continent;
    private String continentKor;
    private String continentKor2;
    private String engName;
    private String korName;

    CountryCode(String alpha3, String numeric, String continent, String continentKor, String continentKor2, String engName, String korName) {
        this.alpha3 = alpha3;
        this.numeric = numeric;
        this.continent = continent;
        this.continentKor = continentKor;
        this.continentKor2 = continentKor2;
        this.engName = engName;
        this.korName = korName;
    }

    @Override
    public String getViewName() {
        return this.korName;
    }

    @Override
    public Long getOrderSeq() {
        return (long) this.ordinal();
    }
}
