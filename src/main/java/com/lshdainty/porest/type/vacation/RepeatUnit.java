package com.lshdainty.porest.type.vacation;

import com.lshdainty.porest.type.DisplayType;

public enum RepeatUnit implements DisplayType {
    YEARLY("매년"),
    MONTHLY("매월"),
    DAYLY("매일"),
    HALF("반기"),
    QUARTERLY("분기");

    private String strName;

    RepeatUnit(String strName) {
        this.strName = strName;
    }

    @Override
    public String getViewName() {return strName;}
}
