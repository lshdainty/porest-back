package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.Holiday;
import com.lshdainty.myhr.domain.HolidayType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepository {
    private final EntityManager em;

    // 신규 휴일 저장
    @Override
    public void save(Holiday holiday) {
        em.persist(holiday);
    }

    // 단건 휴일 조회
    @Override
    public Optional<Holiday> findById(Long seq) {
        return Optional.ofNullable(em.find(Holiday.class, seq));
    }

    // 전체 휴일 조회
    @Override
    public List<Holiday> findHolidays() {
        return em.createQuery("select h from Holiday h order by h.date", Holiday.class)
                .getResultList();
    }

    // 기간에 해당하는 휴일 조회
    @Override
    public List<Holiday> findHolidaysByStartEndDate(String start, String end) {
        return em.createQuery("select h from Holiday h where h.date between :start and :end order by h.date", Holiday.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    // 휴일 타입에 따른 조회
    @Override
    public List<Holiday> findHolidaysByType(HolidayType type) {
        return em.createQuery("select h from Holiday h where h.type = :type", Holiday.class)
                .setParameter("type", type)
                .getResultList();
    }

    @Override
    public List<Holiday> findHolidaysByStartEndDateWithType(String start, String end, HolidayType type) {
        return em.createQuery("select h from Holiday h where h.date between :start and :end and h.type = :type order by h.date", Holiday.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("type", type)
                .getResultList();
    }

    // 휴일 삭제
    @Override
    public void delete(Holiday holiday) {
        em.remove(holiday);
    }
}
