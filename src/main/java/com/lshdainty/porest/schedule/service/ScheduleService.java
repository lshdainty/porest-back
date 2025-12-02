package com.lshdainty.porest.schedule.service;

import com.lshdainty.porest.common.message.MessageKey;
import com.lshdainty.porest.common.util.MessageResolver;
import com.lshdainty.porest.common.util.PorestTime;
import com.lshdainty.porest.schedule.domain.Schedule;
import com.lshdainty.porest.schedule.repository.ScheduleRepositoryImpl;
import com.lshdainty.porest.schedule.service.dto.ScheduleServiceDto;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ScheduleService {
    private final MessageResolver messageResolver;
    private final ScheduleRepositoryImpl scheduleRepositoryImpl;
    private final UserService userService;

    @Transactional
    public Long registSchedule(ScheduleServiceDto data) {
        // 유저 조회
        User user = userService.checkUserExist(data.getUserId());

        if (PorestTime.isAfterThanEndDate(data.getStartDate(), data.getEndDate())) { throw new IllegalArgumentException(messageResolver.getMessage(MessageKey.VALIDATE_START_AFTER_END)); }

        Schedule schedule = Schedule.createSchedule(
                user,
                data.getDesc(),
                data.getType(),
                data.getStartDate(),
                data.getEndDate()
        );

        // 휴가 등록
        scheduleRepositoryImpl.save(schedule);

        return schedule.getId();
    }

    public List<Schedule> searchSchedulesByUser(String userId) {
        return scheduleRepositoryImpl.findSchedulesByUserId(userId);
    }

    public List<Schedule> searchSchedulesByPeriod(LocalDateTime start, LocalDateTime end) {
        if (PorestTime.isAfterThanEndDate(start, end)) { throw new IllegalArgumentException(messageResolver.getMessage(MessageKey.VALIDATE_START_AFTER_END)); }
        return scheduleRepositoryImpl.findSchedulesByPeriod(start, end);
    }

    @Transactional
    public Long updateSchedule(Long scheduleId, ScheduleServiceDto data) {
        // 1. 기존 스케줄 삭제
        deleteSchedule(scheduleId);

        // 2. 새로운 스케줄 등록
        Long newScheduleId = registSchedule(data);

        log.info("스케줄 수정 완료 - 기존 ID: {}, 새로운 ID: {}", scheduleId, newScheduleId);

        return newScheduleId;
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = checkScheduleExist(scheduleId);

        if (schedule.getEndDate().isBefore(LocalDateTime.now())) { throw new IllegalArgumentException(messageResolver.getMessage(MessageKey.VALIDATE_DELETE_BEFORE_NOW)); }

        schedule.deleteSchedule();
    }

    public Schedule checkScheduleExist(Long scheduleId) {
        Optional<Schedule> schedule = scheduleRepositoryImpl.findById(scheduleId);
        if (schedule.isEmpty() || schedule.get().getIsDeleted().equals("Y")) { throw new IllegalArgumentException(messageResolver.getMessage(MessageKey.NOT_FOUND_SCHEDULE)); }
        return schedule.get();
    }
}
