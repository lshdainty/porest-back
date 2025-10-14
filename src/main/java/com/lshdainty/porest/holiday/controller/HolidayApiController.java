package com.lshdainty.porest.holiday.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.holiday.domain.Holiday;
import com.lshdainty.porest.common.type.CountryCode;
import com.lshdainty.porest.holiday.type.HolidayType;
import com.lshdainty.porest.holiday.controller.dto.HolidayApiDto;
import com.lshdainty.porest.holiday.service.HolidayService;
import com.lshdainty.porest.holiday.service.dto.HolidayServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HolidayApiController {
    private final HolidayService holidayService;

    @PostMapping("api/v1/holiday")
    public ApiResponse registHoliday(@RequestBody HolidayApiDto.RegistHolidayReq data) {
        Long holidaySeq = holidayService.registHoliday(HolidayServiceDto.builder()
                .name(data.getHolidayName())
                .date(data.getHolidayDate())
                .type(data.getHolidayType())
                .countryCode(data.getCountryCode())
                .lunarYN(data.getLunarYn())
                .lunarDate(data.getLunarDate())
                .isRecurring(data.getIsRecurring())
                .icon(data.getHolidayIcon())
                .build()
        );
        return ApiResponse.success(new HolidayApiDto.RegistHolidayResp(holidaySeq));
    }

    @GetMapping("api/v1/holidays/date")
    public ApiResponse searchHolidaysByStartEndDate(@RequestParam("start") String start, @RequestParam("end") String end, @RequestParam("country_code") CountryCode countryCode) {
        List<Holiday> holidays = holidayService.searchHolidaysByStartEndDate(start, end, countryCode);

        List<HolidayApiDto.SearchHolidaysResp> resp = holidays.stream()
                .map(h -> new HolidayApiDto.SearchHolidaysResp(
                        h.getSeq(),
                        h.getName(),
                        h.getDate(),
                        h.getType(),
                        h.getCountryCode(),
                        h.getLunarYN(),
                        h.getLunarDate(),
                        h.getIsRecurring(),
                        h.getIcon()
                ))
                .collect(Collectors.toList());

        return ApiResponse.success(resp);
    }

    @GetMapping("api/v1/holidays/type/{type}")
    public ApiResponse searchHolidaysByType(@PathVariable("type") HolidayType type) {
        List<Holiday> holidays = holidayService.searchHolidaysByType(type);

        List<HolidayApiDto.SearchHolidaysResp> resp = holidays.stream()
                .map(h -> new HolidayApiDto.SearchHolidaysResp(
                        h.getSeq(),
                        h.getName(),
                        h.getDate(),
                        h.getType(),
                        h.getCountryCode(),
                        h.getLunarYN(),
                        h.getLunarDate(),
                        h.getIsRecurring(),
                        h.getIcon()
                ))
                .collect(Collectors.toList());

        return ApiResponse.success(resp);
    }

    @PutMapping("/api/v1/holiday/{seq}")
    public ApiResponse editHoliday(@PathVariable("seq") Long seq, @RequestBody HolidayApiDto.EditHolidayReq data) {
        holidayService.editHoliday(HolidayServiceDto.builder()
                .seq(seq)
                .name(data.getHolidayName())
                .date(data.getHolidayDate())
                .type(data.getHolidayType())
                .countryCode(data.getCountryCode())
                .lunarYN(data.getLunarYn())
                .lunarDate(data.getLunarDate())
                .isRecurring(data.getIsRecurring())
                .icon(data.getHolidayIcon())
                .build()
        );

        Holiday findHoliday = holidayService.findById(seq);
        return ApiResponse.success(new HolidayApiDto.EditHolidayResp(
                findHoliday.getSeq(),
                findHoliday.getName(),
                findHoliday.getDate(),
                findHoliday.getType(),
                findHoliday.getCountryCode(),
                findHoliday.getLunarYN(),
                findHoliday.getLunarDate(),
                findHoliday.getIsRecurring(),
                findHoliday.getIcon()
        ));
    }

    @DeleteMapping("/api/v1/holiday/{seq}")
    public ApiResponse deleteHoliday(@PathVariable("seq") Long seq) {
        holidayService.deleteHoliday(seq);
        return ApiResponse.success();
    }
}
