package com.lshdainty.myhr.service;

import com.lshdainty.myhr.domain.*;
import com.lshdainty.myhr.repository.*;
import com.lshdainty.myhr.service.dto.ScheduleServiceDto;
import com.lshdainty.myhr.util.MyhrTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ScheduleService {
    private final MessageSource ms;
    private final ScheduleRepositoryImpl scheduleRepositoryImpl;
    private final HolidayRepositoryImpl holidayRepositoryImpl;
    private final UserService userService;

    @Transactional
    public Long registSchedule(Long userNo, ScheduleType type, String desc, LocalDateTime start, LocalDateTime end, Long addUserNo, String clientIP) {
        // 유저 조회
        User user = userService.checkUserExist(userNo);

        Schedule schedule = Schedule.createSchedule(user, desc, type, start, end, addUserNo, clientIP);

        // 휴가 등록
        scheduleRepositoryImpl.save(schedule);

        return schedule.getId();
    }

    public List<Schedule> findSchedulesByUserNo(Long userNo) {
        return scheduleRepositoryImpl.findSchedulesByUserNo(userNo);
    }

    public List<Schedule> findSchedulesByPeriod(LocalDateTime start, LocalDateTime end) {
        if (MyhrTime.isAfterThanEndDate(start, end)) { throw new IllegalArgumentException(ms.getMessage("error.validate.isAfterThanEndDate", null, null)); }
        return scheduleRepositoryImpl.findSchedulesByPeriod(start, end);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long delUserNo, String clientIP) {
        Schedule schedule = checkScheduleExist(scheduleId);

        if (schedule.getEndDate().isBefore(LocalDateTime.now())) { throw new IllegalArgumentException("Past schedules cannot be deleted"); }

        schedule.deleteSchedule(delUserNo, clientIP);
    }

    public Schedule checkScheduleExist(Long scheduleId) {
        Optional<Schedule> schedule = scheduleRepositoryImpl.findById(scheduleId);
        if (schedule.isEmpty() || schedule.get().getDelYN().equals("Y")) { throw new IllegalArgumentException(ms.getMessage("error.notfound.schedule", null, null)); }
        return schedule.get();
    }

    /**
     * 스케줄에 포함되어있는 휴일, 공휴일을 제외한 후
     * 실제로 사용한 시간을 계산한 후 DTO로
     * 변환 후 반환하는 함수
     *
     * @param schedules vacation에 사용된 스케줄 리스트
     * @return 실제 사용 시간이 포함된 스케줄 DTO 리스트
     */
    public List<ScheduleServiceDto> convertRealUsedTimeDto(List<Schedule> schedules) {
        List<ScheduleServiceDto> scheduleServiceDtos = new ArrayList<>();

        // 공휴일 리스트를 가져오기 위한 startDate 최소값 구하기
        int minStartDate = findScheduleMinStartTime(schedules);
        // 공휴일 리스트를 가져오기 위한 endDate 최대값 구하기
        int maxEndDate = findScheduleMaxEndTime(schedules);
        log.debug("calculated RealUsedTime minStartDate : {}, maxEndDate : {}", minStartDate, maxEndDate);

        List<Holiday> holidays = holidayRepositoryImpl.findHolidaysByStartEndDate(Integer.toString(minStartDate), Integer.toString(maxEndDate));

        // 공휴일 리스트 타입 변경
        List<LocalDate> holidayDates = holidays.stream()
                .map(h -> LocalDate.parse(h.getDate(), DateTimeFormatter.BASIC_ISO_DATE))
                .toList();

        // 사용된 시간 계산
        for (Schedule schedule : schedules) {
            scheduleServiceDtos.add(
                    ScheduleServiceDto.builder()
                            .id(schedule.getId())
                            .user(schedule.getUser())
//                            .vacation(schedule.getVacation())
                            .type(schedule.getType())
                            .desc(schedule.getDesc())
                            .startDate(schedule.getStartDate())
                            .endDate(schedule.getEndDate())
                            .delYN(schedule.getDelYN())
                            .realUsedTime(calculateRealUsed(schedule, holidayDates))
                            .build()
            );
        }

        return scheduleServiceDtos;
    }

    /**
     * 사용자가 실제 사용한 휴가일수를 계산하기 위해
     * 이제까지 사용된 스케줄 중 제일 작은 시작 시간을
     * 찾아내기 위한 함수
     *
     * @param schedules vacation에 사용된 스케줄 리스트
     * @return 제일 작은 시작 시간의 int 값
     */
    public int findScheduleMinStartTime(List<Schedule> schedules) {
        return schedules.stream()
                .map(s -> s.getStartDate().format(DateTimeFormatter.BASIC_ISO_DATE))
                .mapToInt(Integer::parseInt)
                .summaryStatistics()
                .getMin();
    }

    /**
     * 사용자가 실제 사용한 휴가일수를 계산하기 위해
     * 이제까지 사용된 스케줄 중 제일 큰 종료 시간을
     * 찾아내기 위한 함수
     *
     * @param schedules vacation에 사용된 스케줄 리스트
     * @return 제일 큰 종료 시간의 int 값
     */
    public int findScheduleMaxEndTime(List<Schedule> schedules) {
        return schedules.stream()
                .map(s -> s.getEndDate().format(DateTimeFormatter.BASIC_ISO_DATE))
                .mapToInt(Integer::parseInt)
                .summaryStatistics()
                .getMax();
    }

    /**
     * 사용자가 실제 사용한 휴가일수를 계산하기 위한 함수
     * 캘린더에서 드래그해서 휴가를 등록하는 경우 중간에
     * 주말, 공휴일이 포함되는 경우가 있어 제외한 후
     * 실제로 사용한 휴가일수를 알아내기위해 사용함
     *
     * @param schedule 계산 대상 스케줄
     * @param holidays 계산에 필요한 공휴일 리스트
     * @return 공휴일, 주말 등을 제외한 스케줄 실제 사용 시간
     */
    public BigDecimal calculateRealUsed(Schedule schedule, List<LocalDate> holidays) {
//        List<LocalDate> dates = schedule.getBetweenDatesByDayOfWeek(new int[]{6, 7});
//        dates.addAll(holidays);
//        log.debug("remove target dates : {}", dates);
//
//        List<LocalDate> results = schedule.removeAllDates(dates);
//
//        return new BigDecimal("0.0000").add(schedule.getType().convertToValue(results.size()));
        return new BigDecimal("0.0000");
    }
}
