package com.lshdainty.porest.type.vacation;

import com.lshdainty.porest.type.DisplayType;

public enum GrantTiming implements DisplayType {
    FIXED_DATE("고정 날짜"),
    SPECIFIC_MONTH("특정 월"),
    SPECIFIC_DAY("특정 일"),
    QUARTER_END("분기말"),
    HALF_END("반기말"),
    YEAR_END("연말");

    private String strName;

    GrantTiming(String strName) {
        this.strName = strName;
    }

    @Override
    public String getViewName() {return strName;}
}
