package com.lshdainty.porest.calendar.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.porest.vacation.type.VacationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CalendarApiDto {
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class searchEventsByPeriodResp {
        private String userId;
        private String userName;
        private String calendarName;    // vacation, schedule type name
        private String calendarType;    // vacation, schedule type
        private String calendarDesc;    // vacation, schedule desc
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String domainType;      // vacation or schedule
        private VacationType vacationType;  // vacation일 경우 넘어감
        private Long calendarId;
    }
}
