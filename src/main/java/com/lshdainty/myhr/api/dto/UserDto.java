package com.lshdainty.myhr.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter @Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String userId;
    private String userPwd;
    private String userName;
    private String userEmail;
    private String userBirth;
    private String userWorkTime;
    private String userRole;
    private String userEmploy;
    private String lunarYN;
    private String delYN;

    private List<VacationDto> vacations;
}
