package com.lshdainty.myhr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.myhr.domain.Schedule;
import com.lshdainty.myhr.domain.ScheduleType;
import com.lshdainty.myhr.service.dto.ScheduleServiceDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDto {
    private Long scheduleId;
    private Long userNo;
    private String userName;
    private Long vacationId;
    private ScheduleType scheduleType;
    private String scheduleTypeName;
    private String scheduleDesc;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal realUsedTime;

    public ScheduleDto(Long id) {
        scheduleId = id;
    }

    public ScheduleDto(Schedule schedule) {
        scheduleId = schedule.getId();
        userNo = schedule.getUser().getId();
        userName = schedule.getUser().getName();
        if (schedule.getType().isVacationType()) {
            vacationId = schedule.getVacation().getId();
        }
        scheduleType = schedule.getType();
        scheduleTypeName = schedule.getType().getTypeName();
        scheduleDesc = schedule.getDesc();
        startDate = schedule.getStartDate();
        endDate = schedule.getEndDate();
    }

    public ScheduleDto(ScheduleServiceDto schedule) {
        scheduleId = schedule.getId();
        userNo = schedule.getUser().getId();
        userName = schedule.getUser().getName();
        if (schedule.getType().isVacationType()) {
            vacationId = schedule.getVacation().getId();
        }
        scheduleType = schedule.getType();
        scheduleTypeName = schedule.getType().getTypeName();
        scheduleDesc = schedule.getDesc();
        startDate = schedule.getStartDate();
        endDate = schedule.getEndDate();
        realUsedTime = schedule.getRealUsedTime();
    }
}
