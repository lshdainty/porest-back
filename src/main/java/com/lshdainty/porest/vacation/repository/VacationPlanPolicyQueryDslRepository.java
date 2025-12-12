package com.lshdainty.porest.vacation.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.vacation.domain.QVacationPlan;
import com.lshdainty.porest.vacation.domain.QVacationPlanPolicy;
import com.lshdainty.porest.vacation.domain.QVacationPolicy;
import com.lshdainty.porest.vacation.domain.VacationPlanPolicy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * VacationPlanPolicy Repository 구현체<br>
 * QueryDSL을 활용한 플랜-정책 매핑 조회 구현
 */
@Repository
@Primary
@RequiredArgsConstructor
public class VacationPlanPolicyQueryDslRepository implements VacationPlanPolicyRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public void save(VacationPlanPolicy planPolicy) {
        em.persist(planPolicy);
    }

    @Override
    public void saveAll(List<VacationPlanPolicy> planPolicies) {
        for (VacationPlanPolicy planPolicy : planPolicies) {
            em.persist(planPolicy);
        }
    }

    @Override
    public Optional<VacationPlanPolicy> findById(Long id) {
        QVacationPlanPolicy planPolicy = QVacationPlanPolicy.vacationPlanPolicy;
        QVacationPlan plan = QVacationPlan.vacationPlan;
        QVacationPolicy policy = QVacationPolicy.vacationPolicy;

        VacationPlanPolicy result = queryFactory
                .selectFrom(planPolicy)
                .leftJoin(planPolicy.vacationPlan, plan).fetchJoin()
                .leftJoin(planPolicy.vacationPolicy, policy).fetchJoin()
                .where(
                        planPolicy.id.eq(id),
                        planPolicy.isDeleted.eq(YNType.N)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<VacationPlanPolicy> findByPlanId(Long planId) {
        QVacationPlanPolicy planPolicy = QVacationPlanPolicy.vacationPlanPolicy;
        QVacationPolicy policy = QVacationPolicy.vacationPolicy;

        return queryFactory
                .selectFrom(planPolicy)
                .leftJoin(planPolicy.vacationPolicy, policy).fetchJoin()
                .where(
                        planPolicy.vacationPlan.id.eq(planId),
                        planPolicy.isDeleted.eq(YNType.N)
                )
                .orderBy(planPolicy.sortOrder.asc())
                .fetch();
    }

    @Override
    public List<VacationPlanPolicy> findByPolicyId(Long policyId) {
        QVacationPlanPolicy planPolicy = QVacationPlanPolicy.vacationPlanPolicy;
        QVacationPlan plan = QVacationPlan.vacationPlan;

        return queryFactory
                .selectFrom(planPolicy)
                .leftJoin(planPolicy.vacationPlan, plan).fetchJoin()
                .where(
                        planPolicy.vacationPolicy.id.eq(policyId),
                        planPolicy.isDeleted.eq(YNType.N)
                )
                .fetch();
    }

    @Override
    public Optional<VacationPlanPolicy> findByPlanIdAndPolicyId(Long planId, Long policyId) {
        QVacationPlanPolicy planPolicy = QVacationPlanPolicy.vacationPlanPolicy;
        QVacationPlan plan = QVacationPlan.vacationPlan;
        QVacationPolicy policy = QVacationPolicy.vacationPolicy;

        VacationPlanPolicy result = queryFactory
                .selectFrom(planPolicy)
                .leftJoin(planPolicy.vacationPlan, plan).fetchJoin()
                .leftJoin(planPolicy.vacationPolicy, policy).fetchJoin()
                .where(
                        planPolicy.vacationPlan.id.eq(planId),
                        planPolicy.vacationPolicy.id.eq(policyId),
                        planPolicy.isDeleted.eq(YNType.N)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByPlanIdAndPolicyId(Long planId, Long policyId) {
        QVacationPlanPolicy planPolicy = QVacationPlanPolicy.vacationPlanPolicy;

        Integer result = queryFactory
                .selectOne()
                .from(planPolicy)
                .where(
                        planPolicy.vacationPlan.id.eq(planId),
                        planPolicy.vacationPolicy.id.eq(policyId),
                        planPolicy.isDeleted.eq(YNType.N)
                )
                .fetchFirst();

        return result != null;
    }
}
