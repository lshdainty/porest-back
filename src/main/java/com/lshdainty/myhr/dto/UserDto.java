package com.lshdainty.myhr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.myhr.domain.User;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long userNo;
    private String userName;
    private String userBirth;
    private String userWorkTime;
    private String userEmploy;
    private String lunarYN;
    private String delYN;

    private List<VacationDto> vacations;
}
