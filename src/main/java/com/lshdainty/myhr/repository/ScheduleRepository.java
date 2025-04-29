package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.Schedule;
import com.lshdainty.myhr.domain.Vacation;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository {
    void save(Schedule schedule);
    Schedule findById(Long scheduleId);
    List<Schedule> findSchedulesByUserNo(Long userNo);
    List<Schedule> findSchedulesByPeriod(LocalDateTime start, LocalDateTime end);
    List<Schedule> findSchedulesByVacation(Vacation vacation);
}
