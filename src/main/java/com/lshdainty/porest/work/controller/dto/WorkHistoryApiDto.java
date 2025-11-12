package com.lshdainty.porest.work.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkHistoryApiDto {
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CreateWorkHistoryReq {
        private LocalDate workDate;
        private Long workUser;
        private Long workGroup;
        private Long workPart;
        private Long workClass;
        private BigDecimal workHour;
        private String workContent;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CreateWorkHistoryResp {
        private Long workHistorySeq;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UpdateWorkHistoryReq {
        private LocalDate workDate;
        private Long workUser;
        private Long workGroup;
        private Long workPart;
        private Long workClass;
        private BigDecimal workHour;
        private String workContent;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class WorkHistoryResp {
        private Long workHistorySeq;
        private LocalDate workDate;
        private String workUserName;
        private String workGroupName;
        private String workPartName;
        private String workClassName;
        private BigDecimal workHour;
        private String workContent;
    }
}
