package com.lshdainty.porest.work.type;

import com.lshdainty.porest.common.type.DisplayType;

public enum SystemType implements DisplayType {
    ERP("ERP 시스템", 1L),
    MES("생산관리 시스템", 2L),
    WMS("창고관리 시스템", 3L),
    SCM("공급망관리 시스템", 4L),
    CRM("고객관리 시스템", 5L),
    HRM("인사관리 시스템", 6L),
    FINANCE("재무회계 시스템", 7L),
    PROJECT("프로젝트관리 시스템", 8L),
    QUALITY("품질관리 시스템", 9L),
    SALES("영업관리 시스템", 10L),
    PURCHASE("구매관리 시스템", 11L),
    ASSET("자산관리 시스템", 12L),
    BI("비즈니스 인텔리전스", 13L),
    PORTAL("포털 시스템", 14L),
    GROUPWARE("그룹웨어", 15L),
    ETC("기타", 99L);

    private String strName;
    private Long orderSeq;

    SystemType(String strName, Long orderSeq) {
        this.strName = strName;
        this.orderSeq = orderSeq;
    }

    @Override
    public String getViewName() {
        return strName;
    }

    @Override
    public Long getOrderSeq() {
        return orderSeq;
    }
}
