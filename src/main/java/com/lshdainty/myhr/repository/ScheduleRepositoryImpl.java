package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.Schedule;
import com.lshdainty.myhr.domain.Vacation;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {
    private final EntityManager em;

    // 신규 스케쥴 저장
    @Override
    public void save(Schedule schedule) {
        em.persist(schedule);
    }

    // 스케줄 단건 조회(delete용)
    @Override
    public Optional<Schedule> findById(Long scheduleId) {
        return Optional.ofNullable(em.find(Schedule.class, scheduleId));
    }

    // 유저 스케줄 조회
    @Override
    public List<Schedule> findSchedulesByUserNo(Long userNo) {
        return em.createQuery("select s from Schedule s where s.user.id = :userNo and s.delYN = :delYN", Schedule.class)
                .setParameter("userNo", userNo)
                .setParameter("delYN", "N")
                .getResultList();
    }

    // 기간에 해당하는 스케줄 조회
    @Override
    public List<Schedule> findSchedulesByPeriod(LocalDateTime start, LocalDateTime end) {
        return em.createQuery("select s from Schedule s where s.startDate between :start and :end and s.delYN = :delYN", Schedule.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("delYN", "N")
                .getResultList();
    }

    // 휴가에 속한 스케줄 조회
    @Override
    public List<Schedule> findSchedulesByVacation(Vacation vacation) {
        return em.createQuery("select s from Schedule s where s.vacation.id = :vacationId and s.delYN = :delYN", Schedule.class)
                .setParameter("vacationId", vacation.getId())
                .setParameter("delYN", "N")
                .getResultList();
    }
}
