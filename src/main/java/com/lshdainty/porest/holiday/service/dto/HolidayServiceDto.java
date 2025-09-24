package com.lshdainty.porest.holiday.service.dto;

import com.lshdainty.porest.common.type.CountryCode;
import com.lshdainty.porest.holiday.type.HolidayType;
import com.lshdainty.porest.common.type.YNType;
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
    private CountryCode countryCode;
    private YNType lunarYN;
    private String lunarDate;
    private YNType isRecurring;
    private String icon;
}