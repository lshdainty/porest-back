package com.lshdainty.porest.type;

public enum HolidayType implements DisplayType {
    PUBLIC("공휴일"),
    SUBSTITUTE("대체"),
    ETC("기타");

    private String strName;

    HolidayType(String strName) {
        this.strName = strName;
    }

    @Override
    public String getViewName() {
        return this.strName;
    }
}