package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.VacationHistory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VacationHistoryRepositoryImpl implements VacationHistoryRepository {
    private final EntityManager em;

    @Override
    public void save(VacationHistory vacationHistory) {
        em.persist(vacationHistory);
    }
}
