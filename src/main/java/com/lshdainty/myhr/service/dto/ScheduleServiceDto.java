package com.lshdainty.myhr.service.dto;

import com.lshdainty.myhr.domain.ScheduleType;
import com.lshdainty.myhr.domain.User;
import com.lshdainty.myhr.domain.Vacation;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleServiceDto {
    private Long id;
    private User user;
    private Vacation vacation;
    private ScheduleType type;
    private String desc;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String delYN;
    private BigDecimal realUsedTime;

    @Override
    public String toString() {
        return "ScheduleServiceDto{" +
                "id: " + id +
                ", type: " + type +
                ", desc: '" + desc + '\'' +
                ", startDate: " + startDate +
                ", endDate: " + endDate +
                ", delYN: '" + delYN + '\'' +
                ", realUsedTime: " + realUsedTime +
                '}';
    }
}