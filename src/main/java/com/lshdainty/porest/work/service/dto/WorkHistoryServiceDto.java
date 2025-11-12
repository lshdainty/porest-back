package com.lshdainty.porest.work.service.dto;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.work.domain.WorkCode;
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
    private User user;
    private WorkCode group;
    private WorkCode part;
    private WorkCode classes;
    private BigDecimal hours;
    private String content;
}
