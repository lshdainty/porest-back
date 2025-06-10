package com.lshdainty.myhr.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum VacationTimeType {
    DAYOFF("연차", 24*60*60L-1L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(1.0000))),
    MORNINGOFF("오전반차", 4*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.5000))),
    AFTERNOONOFF("오후반차", 4*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.5000))),
    ONETIMEOFF("1시간 휴가", 1*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.1250))),
    TWOTIMEOFF("2시간 휴가", 2*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.2500))),
    THREETIMEOFF("3시간 휴가", 3*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.3750))),
    FIVETIMEOFF("5시간 휴가", 5*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.6250))),
    SIXTIMEOFF("6시간 휴가", 6*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.7500))),
    SEVENTIMEOFF("7시간 휴가", 7*60*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.8750))),
    HALFTIMEOFF("30분 휴가", 30*60L, dayDiff -> dayDiff.multiply(BigDecimal.valueOf(0.0625)));

    private String strName;
    private Long seconds;
    private Function<BigDecimal, BigDecimal> expression;

    VacationTimeType(String strName, Long seconds, Function<BigDecimal, BigDecimal> expression) {
        this.strName = strName;
        this.seconds = seconds;
        this.expression = expression;

    }

    public Long getSeconds() {
        return seconds;
    }

    public BigDecimal convertToValue(int dayCount) {
        return expression.apply(BigDecimal.valueOf(dayCount));
    }
}
