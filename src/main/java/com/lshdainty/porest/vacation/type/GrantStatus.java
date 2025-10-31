package com.lshdainty.porest.vacation.type;

import com.lshdainty.porest.common.type.DisplayType;

public enum GrantStatus implements DisplayType {
    ACTIVE("활성"),
    EXHAUSTED("소진"),
    EXPIRED("만료"),
    REVOKED("회수");

    private String strName;

    GrantStatus(String strName) {
        this.strName = strName;
    }

    @Override
    public String getViewName() {return strName;}
}
