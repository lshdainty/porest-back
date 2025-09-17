package com.lshdainty.porest.type.vacation;

import com.lshdainty.porest.type.DisplayType;

public enum GrantMethod implements DisplayType {
    ON_REQUEST("신청시 부여"),
    MANUAL_GRANT("관리자 직접 부여"),
    REPEAT_GRANT("반복 부여");

    private String strName;

    GrantMethod(String strName) {
        this.strName = strName;
    }

    @Override
    public String getViewName() {return strName;}
};