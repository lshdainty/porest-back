package com.lshdainty.porest.vacation.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.vacation.domain.VacationPlanPolicy;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * VacationPlanPolicy Repository 구현체 (JPQL)<br>
 * JPQL을 활용한 플랜-정책 매핑 조회 구현 (백업용)
 */
@Repository("vacationPlanPolicyJpaRepository")
@RequiredArgsConstructor
public class VacationPlanPolicyJpaRepository implements VacationPlanPolicyRepository {
    private final EntityManager em;

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
        List<VacationPlanPolicy> result = em.createQuery(
                "select vpp from VacationPlanPolicy vpp " +
                "left join fetch vpp.vacationPlan vp " +
                "left join fetch vpp.vacationPolicy p " +
                "where vpp.id = :id and vpp.isDeleted = :isDeleted", VacationPlanPolicy.class)
                .setParameter("id", id)
                .setParameter("isDeleted", YNType.N)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<VacationPlanPolicy> findByPlanId(Long planId) {
        return em.createQuery(
                "select vpp from VacationPlanPolicy vpp " +
                "left join fetch vpp.vacationPolicy p " +
                "where vpp.vacationPlan.id = :planId and vpp.isDeleted = :isDeleted " +
                "order by vpp.sortOrder asc", VacationPlanPolicy.class)
                .setParameter("planId", planId)
                .setParameter("isDeleted", YNType.N)
                .getResultList();
    }

    @Override
    public List<VacationPlanPolicy> findByPolicyId(Long policyId) {
        return em.createQuery(
                "select vpp from VacationPlanPolicy vpp " +
                "left join fetch vpp.vacationPlan vp " +
                "where vpp.vacationPolicy.id = :policyId and vpp.isDeleted = :isDeleted", VacationPlanPolicy.class)
                .setParameter("policyId", policyId)
                .setParameter("isDeleted", YNType.N)
                .getResultList();
    }

    @Override
    public Optional<VacationPlanPolicy> findByPlanIdAndPolicyId(Long planId, Long policyId) {
        List<VacationPlanPolicy> result = em.createQuery(
                "select vpp from VacationPlanPolicy vpp " +
                "left join fetch vpp.vacationPlan vp " +
                "left join fetch vpp.vacationPolicy p " +
                "where vpp.vacationPlan.id = :planId and vpp.vacationPolicy.id = :policyId " +
                "and vpp.isDeleted = :isDeleted", VacationPlanPolicy.class)
                .setParameter("planId", planId)
                .setParameter("policyId", policyId)
                .setParameter("isDeleted", YNType.N)
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public boolean existsByPlanIdAndPolicyId(Long planId, Long policyId) {
        List<VacationPlanPolicy> result = em.createQuery(
                "select vpp from VacationPlanPolicy vpp " +
                "where vpp.vacationPlan.id = :planId and vpp.vacationPolicy.id = :policyId " +
                "and vpp.isDeleted = :isDeleted", VacationPlanPolicy.class)
                .setParameter("planId", planId)
                .setParameter("policyId", policyId)
                .setParameter("isDeleted", YNType.N)
                .setMaxResults(1)
                .getResultList();
        return !result.isEmpty();
    }
}
