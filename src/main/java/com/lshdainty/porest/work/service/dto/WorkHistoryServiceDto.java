package com.lshdainty.porest.work.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@Builder
public class WorkHistoryServiceDto {
    private Long seq;
    private LocalDate date;
    private String userId;
    private String groupCode;
    private String partCode;
    private String classCode;
    private BigDecimal hours;
    private String content;

    // 조회용 필드
    private String userName;
    private String groupName;
    private String partName;
    private String className;
}
