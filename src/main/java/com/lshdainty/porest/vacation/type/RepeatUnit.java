package com.lshdainty.porest.vacation.type;

import com.lshdainty.porest.common.type.DisplayType;

public enum RepeatUnit implements DisplayType {
    YEARLY("매년"),
    MONTHLY("매월"),
    DAILY("매일"),
    HALF("반기"),
    QUARTERLY("분기");

    private String strName;

    RepeatUnit(String strName) {
        this.strName = strName;
    }

    @Override
    public String getViewName() {return strName;}
}
