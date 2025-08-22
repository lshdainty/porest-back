package com.lshdainty.myhr.service.dto;

import com.lshdainty.myhr.type.HolidayType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class HolidayServiceDto {
    private Long seq;
    private String name;
    private String date;
    private HolidayType type;
}