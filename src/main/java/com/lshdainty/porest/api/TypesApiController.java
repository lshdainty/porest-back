package com.lshdainty.porest.api;

import com.lshdainty.porest.api.dto.TypesDto;
import com.lshdainty.porest.type.DisplayType;
import com.lshdainty.porest.type.HolidayType;
import com.lshdainty.porest.type.ScheduleType;
import com.lshdainty.porest.type.vacation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final Map<String, Class<? extends DisplayType>> enumMap = Map.of(
            "grant-method", GrantMethod.class,
            "grant-timing", GrantTiming.class,
            "repeat-unit", RepeatUnit.class,
            "vacation-time", VacationTimeType.class,
            "vacation-type", VacationType.class,
            "schedule-type", ScheduleType.class,
            "holiday-type", HolidayType.class

    );

    @GetMapping("/api/v1/types/{enumName}")
    public ApiResponse<List<TypesDto>> getEnumValues(@PathVariable String enumName) {
        Class<? extends DisplayType> enumClass = enumMap.get(enumName.toLowerCase());

        if (enumClass == null) {
            // 요청된 Enum이 없을 경우 404 Not Found 반환
            throw new IllegalArgumentException("");
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
