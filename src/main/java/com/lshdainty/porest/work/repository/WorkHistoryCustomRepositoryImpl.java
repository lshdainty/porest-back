package com.lshdainty.porest.work.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.work.domain.WorkHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.lshdainty.porest.work.domain.QWorkHistory.workHistory;

@Repository
@RequiredArgsConstructor
public class WorkHistoryCustomRepositoryImpl implements WorkHistoryCustomRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    @Override
    public void save(WorkHistory workHistory) {
        em.persist(workHistory);
    }

    @Override
    public Optional<WorkHistory> findById(Long id) {
        return Optional.ofNullable(query
                .selectFrom(workHistory)
                .join(workHistory.user).fetchJoin()
                .leftJoin(workHistory.group).fetchJoin()
                .leftJoin(workHistory.part).fetchJoin()
                .leftJoin(workHistory.classes).fetchJoin()
                .where(workHistory.seq.eq(id))
                .fetchOne()
        );
    }

    @Override
    public List<WorkHistory> findAll() {
        return query
                .selectFrom(workHistory)
                .join(workHistory.user).fetchJoin()
                .leftJoin(workHistory.group).fetchJoin()
                .leftJoin(workHistory.part).fetchJoin()
                .leftJoin(workHistory.classes).fetchJoin()
                .where(workHistory.isDeleted.eq(YNType.N))
                .orderBy(workHistory.date.desc())
                .fetch();
    }

    @Override
    public void delete(WorkHistory workHistory) {
        workHistory.deleteWorkHistory();
    }
}
