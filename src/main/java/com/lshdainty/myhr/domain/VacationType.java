package com.lshdainty.myhr.domain;

import java.time.LocalDateTime;

public enum VacationType {
    ANNUAL("연차"),
    MATERNITY("출산"),
    WEDDING("결혼"),
    BEREAVEMENT("상조"),
    OVERTIME("연장");

    private String strName;

    VacationType(String strName) {
        this.strName = strName;
    }

    // 비즈니스 편의 메소드
    /**
    * 휴가 타입에 대한 날짜 검색 범위 중 시작 날짜를 반환 함수</br>
    * 연차 및 연장 휴가의 경우 회계연도(1년 기준)으로 날짜 검색</br>
    * 출산, 결혼, 상조는 날짜 검색 제외(그룹핑 하지 않고 각각 개별적으로 사용)
    * @param occurDate
    * @return startDate
    */
    public LocalDateTime getSelectStartDate(LocalDateTime occurDate) {
        if (this.equals(VacationType.ANNUAL) || this.equals(VacationType.OVERTIME)) {
            return LocalDateTime.of(occurDate.getYear(), 1, 1, 0, 0, 0);
        } else {
            return null;
        }
    }

    /**
     * 휴가 타입에 대한 날짜 검색 범위 중 종료 날짜를 반환 함수</br>
     * 연차 및 연장 휴가의 경우 회계연도(1년 기준)으로 날짜 검색</br>
     * 출산, 결혼, 상조는 날짜 검색 제외(그룹핑 하지 않고 각각 개별적으로 사용)
     * @param occurDate
     * @return startDate
     */
    public LocalDateTime getSelectEndDate(LocalDateTime occurDate) {
        if (this.equals(VacationType.ANNUAL) || this.equals(VacationType.OVERTIME)) {
            return LocalDateTime.of(occurDate.getYear(), 12, 31, 23, 59, 59);
        } else {
            return null;
        }
    }
}
