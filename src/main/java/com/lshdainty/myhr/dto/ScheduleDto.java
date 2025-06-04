package com.lshdainty.myhr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.myhr.domain.ScheduleType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDto {
    private Long scheduleId;
    private Long userNo;
    private String userName;
    private ScheduleType scheduleType;
    private String scheduleTypeName;
    private String scheduleDesc;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal realUsedTime;
}
