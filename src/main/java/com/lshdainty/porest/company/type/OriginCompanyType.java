package com.lshdainty.porest.company.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OriginCompanyType {
    SKAX("SK AX"),
    DTOL("디투엘"),
    INSIGHTON("인사이트온"),
    BIGXDATA("BigxData"),
    CNTHOTH("씨앤토트플러스"),
    AGS("AGS");

    private String companyName;

    OriginCompanyType(String companyName) { this.companyName = companyName; }
}
