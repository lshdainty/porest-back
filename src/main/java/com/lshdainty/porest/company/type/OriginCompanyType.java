package com.lshdainty.porest.company.type;

import com.lshdainty.porest.common.type.DisplayType;

public enum OriginCompanyType implements DisplayType {
    SKAX("SK AX"),
    DTOL("디투엘"),
    INSIGHTON("인사이트온"),
    BIGXDATA("BigxData"),
    CNTHOTH("씨앤토트플러스"),
    AGS("AGS");

    private String companyName;

    OriginCompanyType(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String getViewName() {
        return companyName;
    }
}
