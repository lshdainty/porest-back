package com.lshdainty.porest.common.controller;

import com.lshdainty.porest.common.controller.dto.TypesDto;
import com.lshdainty.porest.common.type.DisplayType;
import com.lshdainty.porest.company.type.OriginCompanyType;
import com.lshdainty.porest.holiday.type.HolidayType;
import com.lshdainty.porest.schedule.type.ScheduleType;
import com.lshdainty.porest.vacation.type.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TypesApiController {
    private final MessageSource ms;

    private final Map<String, Class<? extends DisplayType>> enumMap = Map.of(
            "grant-method", GrantMethod.class,
            "repeat-unit", RepeatUnit.class,
            "vacation-time", VacationTimeType.class,
            "vacation-type", VacationType.class,
            "schedule-type", ScheduleType.class,
            "holiday-type", HolidayType.class,
            "origin-company-type", OriginCompanyType.class
    );

    @GetMapping("/api/v1/types/{enumName}")
    public ApiResponse<List<TypesDto>> getEnumValues(@PathVariable String enumName) {
        Class<? extends DisplayType> enumClass = enumMap.get(enumName.toLowerCase());

        if (enumClass == null) {
            throw new IllegalArgumentException(ms.getMessage("error.notfound.type", null, null));
        }

        List<TypesDto> enumValues = Arrays.stream(enumClass.getEnumConstants())
                .map(enumConstant -> TypesDto.builder()
                        .code(((Enum<?>) enumConstant).name())
                        .name(((DisplayType) enumConstant).getViewName())
                        .build()
                )
                .toList();

        return ApiResponse.success(enumValues);
    }
}
