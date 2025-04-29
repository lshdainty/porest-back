package com.lshdainty.myhr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.myhr.domain.Schedule;
import com.lshdainty.myhr.domain.Vacation;
import com.lshdainty.myhr.domain.VacationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacationDto {
    private Long vacationId;
    private String vacationName;
    private String vacationDesc;
    private VacationType vacationType;
    private BigDecimal grantTime;
    private LocalDateTime occurDate;
    private LocalDateTime expiryDate;
    private BigDecimal usedTime;
    private BigDecimal remainTime;
    private List<ScheduleDto> schedules;

    private Long userNo;

    public VacationDto(Long vacationId) {
        this.vacationId = vacationId;
    }

    public VacationDto(Vacation vacation) {
        vacationId = vacation.getId();
        vacationName = vacation.getName();
        vacationDesc = vacation.getDesc();
        vacationType = vacation.getType();
        grantTime = vacation.getGrantTime();
        occurDate = vacation.getOccurDate();
        expiryDate = vacation.getExpiryDate();
    }

    public VacationDto(Vacation vacation, Boolean flag) {
        vacationId = vacation.getId();
        vacationName = vacation.getName();
        vacationDesc = vacation.getDesc();
        vacationType = vacation.getType();
        grantTime = vacation.getGrantTime();
        occurDate = vacation.getOccurDate();
        expiryDate = vacation.getExpiryDate();

        schedules = vacation.getSchedules().stream().map(
                schedule -> new ScheduleDto(schedule)).collect(Collectors.toList());

        usedTime = BigDecimal.ZERO;
        remainTime = BigDecimal.ZERO;

//        for (ScheduleDto schedule : schedules) {
//            usedTime = usedTime.add(schedule.getScheduleType().convertToValue());
//        }
    }
}
