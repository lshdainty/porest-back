package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.VacationHistory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VacationHistoryRepositoryImpl implements VacationHistoryRepository {
    private final EntityManager em;

    @Override
    public void save(VacationHistory vacationHistory) {
        em.persist(vacationHistory);
    }

    @Override
    public Optional<VacationHistory> findById(Long vacationHistoryId) {
        return Optional.ofNullable(em.find(VacationHistory.class, vacationHistoryId));
    }

    @Override
    public List<VacationHistory> findVacationHistorysByPeriod(LocalDateTime start, LocalDateTime end) {
        return em.createQuery("select vh from VacationHistory vh where vh.type is not null and vh.usedDateTime between :start and :end and vh.delYN = :delYN order by vh.vacation.id, vh.usedDateTime", VacationHistory.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("delYN", "N")
                .getResultList();
    }
}
