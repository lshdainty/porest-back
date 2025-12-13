package com.lshdainty.porest.vacation.repository;

import com.lshdainty.porest.vacation.domain.VacationPlanPolicy;

import java.util.List;
import java.util.Optional;

/**
 * VacationPlanPolicy Repository Interface<br>
 * QueryDSL을 활용한 플랜-정책 매핑 조회 인터페이스
 */
public interface VacationPlanPolicyRepository {
    /**
     * 신규 플랜-정책 매핑 저장
     *
     * @param planPolicy 저장할 플랜-정책 매핑
     */
    void save(VacationPlanPolicy planPolicy);

    /**
     * 플랜-정책 매핑 일괄 저장
     *
     * @param planPolicies 저장할 플랜-정책 매핑 목록
     */
    void saveAll(List<VacationPlanPolicy> planPolicies);

    /**
     * 플랜-정책 매핑 ID로 조회
     *
     * @param id 매핑 ID
     * @return Optional<VacationPlanPolicy>
     */
    Optional<VacationPlanPolicy> findById(Long id);

    /**
     * 플랜 ID로 플랜-정책 매핑 목록 조회
     *
     * @param planId 플랜 ID
     * @return List<VacationPlanPolicy>
     */
    List<VacationPlanPolicy> findByPlanId(Long planId);

    /**
     * 정책 ID로 플랜-정책 매핑 목록 조회
     *
     * @param policyId 정책 ID
     * @return List<VacationPlanPolicy>
     */
    List<VacationPlanPolicy> findByPolicyId(Long policyId);

    /**
     * 플랜 ID와 정책 ID로 플랜-정책 매핑 조회
     *
     * @param planId 플랜 ID
     * @param policyId 정책 ID
     * @return Optional<VacationPlanPolicy>
     */
    Optional<VacationPlanPolicy> findByPlanIdAndPolicyId(Long planId, Long policyId);

    /**
     * 플랜 ID와 정책 ID로 매핑 존재 여부 확인
     *
     * @param planId 플랜 ID
     * @param policyId 정책 ID
     * @return 존재 여부
     */
    boolean existsByPlanIdAndPolicyId(Long planId, Long policyId);
}
