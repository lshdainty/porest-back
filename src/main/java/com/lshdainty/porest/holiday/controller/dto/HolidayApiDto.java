package com.lshdainty.porest.holiday.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.porest.common.type.CountryCode;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.holiday.type.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class HolidayApiDto {
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistHolidayReq {
        private String holidayName;
        private String holidayDate;
        private HolidayType holidayType;
        private CountryCode countryCode;
        private YNType lunarYn;
        private String lunarDate;
        private YNType isRecurring;
        private String holidayIcon;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistHolidayResp {
        private Long holidaySeq;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditHolidayReq {
        private String holidayName;
        private String holidayDate;
        private HolidayType holidayType;
        private CountryCode countryCode;
        private YNType lunarYn;
        private String lunarDate;
        private YNType isRecurring;
        private String holidayIcon;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchHolidaysResp {
        private Long holidaySeq;
        private String holidayName;
        private String holidayDate;
        private HolidayType holidayType;
        private CountryCode countryCode;
        private YNType lunarYn;
        private String lunarDate;
        private YNType isRecurring;
        private String holidayIcon;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditHolidayResp {
        private Long holidaySeq;
        private String holidayName;
        private String holidayDate;
        private HolidayType holidayType;
        private CountryCode countryCode;
        private YNType lunarYn;
        private String lunarDate;
        private YNType isRecurring;
        private String holidayIcon;
    }
}