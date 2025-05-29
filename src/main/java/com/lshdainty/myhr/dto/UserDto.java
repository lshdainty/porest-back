package com.lshdainty.myhr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.myhr.domain.User;
import com.lshdainty.myhr.domain.VacationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
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

    private BigDecimal standardTime; // 기본 휴가
    private BigDecimal addedTime;    // 추가 휴가
    private List<VacationDto> vacations;

    public UserDto(Long no) {
        this.userNo = no;
    }

    public UserDto(User user) {
        this.userNo = user.getId();
        this.userName = user.getName();
        this.userBirth = user.getBirth();
        this.userWorkTime = user.getWorkTime();
        this.userEmploy = user.getEmploy();
        this.lunarYN = user.getLunarYN();
        this.delYN = user.getDelYN();
    }

    public UserDto(User user, List<VacationDto> vacations) {
        this.userNo = user.getId();
        this.userName = user.getName();
        this.standardTime = new BigDecimal(0);
        this.addedTime = new BigDecimal(0);
        this.vacations = vacations;

//        this.vacations.forEach(v -> {
//            if (v.getVacationType().equals(VacationType.BASIC)) {
//                this.standardTime = getStandardTime().add(v.getGrantTime());
//            } else if (v.getVacationType().equals(VacationType.ADDED)) {
//                this.addedTime = getAddedTime().add(v.getGrantTime());
//            }
//        });
    }
}
