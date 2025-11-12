package com.lshdainty.porest.work.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.work.domain.WorkHistory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkHistoryRepositoryImpl implements WorkHistoryRepository {
    private final EntityManager em;

    @Override
    public void save(WorkHistory workHistory) {
        em.persist(workHistory);
    }

    @Override
    public Optional<WorkHistory> findById(Long id) {
        return Optional.ofNullable(em.find(WorkHistory.class, id));
    }

    @Override
    public List<WorkHistory> findAll() {
        return em.createQuery("select w from WorkHistory w where w.isDeleted = :isDeleted order by w.date desc", WorkHistory.class)
                .setParameter("isDeleted", YNType.N)
                .getResultList();
    }

    @Override
    public void delete(WorkHistory workHistory) {
        workHistory.deleteWorkHistory();
    }
}
