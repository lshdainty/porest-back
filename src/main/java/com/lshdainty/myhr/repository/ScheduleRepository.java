package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.Schedule;
import com.lshdainty.myhr.domain.Vacation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    void save(Schedule schedule);
    Optional<Schedule> findById(Long scheduleId);
    List<Schedule> findSchedulesByUserNo(Long userNo);
    List<Schedule> findSchedulesByPeriod(LocalDateTime start, LocalDateTime end);
    List<Schedule> findSchedulesByVacation(Vacation vacation);
}
