package com.lshdainty.porest.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.vacation.domain.VacationPlan;
import com.lshdainty.porest.vacation.domain.VacationPlanPolicy;
import com.lshdainty.porest.vacation.domain.VacationPolicy;
import com.lshdainty.porest.vacation.repository.VacationPlanPolicyJpaRepository;
import com.lshdainty.porest.vacation.type.EffectiveType;
import com.lshdainty.porest.vacation.type.ExpirationType;
import com.lshdainty.porest.vacation.type.VacationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({VacationPlanPolicyJpaRepository.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPQL 휴가 플랜-정책 매핑 레포지토리 테스트")
class VacationPlanPolicyJpaRepositoryTest {
    @Autowired
    private VacationPlanPolicyJpaRepository vacationPlanPolicyRepository;

    @Autowired
    private TestEntityManager em;

    private VacationPlan plan;
    private VacationPolicy policy;

    @BeforeEach
    void setUp() {
        plan = VacationPlan.createPlan("DEFAULT", "기본 플랜", "기본 휴가 플랜");
        policy = VacationPolicy.createManualGrantPolicy(
                "연차", "연차 정책", VacationType.ANNUAL, new BigDecimal("15.0"),
                YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
        );
        em.persist(plan);
        em.persist(policy);
    }

    @Nested
    @DisplayName("save")
    class Save {
        @Test
        @DisplayName("플랜-정책 매핑 저장 성공")
        void saveSuccess() {
            // given
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.N);

            // when
            vacationPlanPolicyRepository.save(planPolicy);
            em.flush();
            em.clear();

            // then
            Optional<VacationPlanPolicy> findPlanPolicy = vacationPlanPolicyRepository.findById(planPolicy.getId());
            assertThat(findPlanPolicy).isPresent();
            assertThat(findPlanPolicy.get().getSortOrder()).isEqualTo(1);
            assertThat(findPlanPolicy.get().getIsDefault()).isEqualTo(YNType.N);
        }
    }

    @Nested
    @DisplayName("saveAll")
    class SaveAll {
        @Test
        @DisplayName("여러 플랜-정책 매핑 일괄 저장 성공")
        void saveAllSuccess() {
            // given
            VacationPolicy policy2 = VacationPolicy.createManualGrantPolicy(
                    "건강휴가", "건강휴가 정책", VacationType.HEALTH, new BigDecimal("3.0"),
                    YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
            );
            em.persist(policy2);

            VacationPlanPolicy planPolicy1 = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.Y);
            VacationPlanPolicy planPolicy2 = VacationPlanPolicy.createPlanPolicy(plan, policy2, 2, YNType.N);

            // when
            vacationPlanPolicyRepository.saveAll(List.of(planPolicy1, planPolicy2));
            em.flush();
            em.clear();

            // then
            List<VacationPlanPolicy> planPolicies = vacationPlanPolicyRepository.findByPlanId(plan.getId());
            assertThat(planPolicies).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("ID로 플랜-정책 매핑 조회 성공")
        void findByIdSuccess() {
            // given
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.N);
            vacationPlanPolicyRepository.save(planPolicy);
            em.flush();
            em.clear();

            // when
            Optional<VacationPlanPolicy> findPlanPolicy = vacationPlanPolicyRepository.findById(planPolicy.getId());

            // then
            assertThat(findPlanPolicy).isPresent();
            assertThat(findPlanPolicy.get().getVacationPlan().getCode()).isEqualTo("DEFAULT");
            assertThat(findPlanPolicy.get().getVacationPolicy().getName()).isEqualTo("연차");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void findByIdNotFound() {
            // given & when
            Optional<VacationPlanPolicy> findPlanPolicy = vacationPlanPolicyRepository.findById(999L);

            // then
            assertThat(findPlanPolicy).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회되지 않음")
        void findByIdDeletedPlanPolicy() {
            // given
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.N);
            vacationPlanPolicyRepository.save(planPolicy);
            planPolicy.deletePlanPolicy();
            em.flush();
            em.clear();

            // when
            Optional<VacationPlanPolicy> findPlanPolicy = vacationPlanPolicyRepository.findById(planPolicy.getId());

            // then
            assertThat(findPlanPolicy).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByPlanId")
    class FindByPlanId {
        @Test
        @DisplayName("플랜 ID로 매핑 목록 조회 성공")
        void findByPlanIdSuccess() {
            // given
            VacationPolicy policy2 = VacationPolicy.createManualGrantPolicy(
                    "건강휴가", "건강휴가 정책", VacationType.HEALTH, new BigDecimal("3.0"),
                    YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
            );
            em.persist(policy2);

            VacationPlanPolicy planPolicy1 = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.Y);
            VacationPlanPolicy planPolicy2 = VacationPlanPolicy.createPlanPolicy(plan, policy2, 2, YNType.N);
            vacationPlanPolicyRepository.saveAll(List.of(planPolicy1, planPolicy2));
            em.flush();
            em.clear();

            // when
            List<VacationPlanPolicy> planPolicies = vacationPlanPolicyRepository.findByPlanId(plan.getId());

            // then
            assertThat(planPolicies).hasSize(2);
            assertThat(planPolicies).extracting("sortOrder").containsExactly(1, 2); // sortOrder 오름차순
        }

        @Test
        @DisplayName("플랜에 매핑된 정책이 없으면 빈 리스트 반환")
        void findByPlanIdEmpty() {
            // given
            em.flush();
            em.clear();

            // when
            List<VacationPlanPolicy> planPolicies = vacationPlanPolicyRepository.findByPlanId(plan.getId());

            // then
            assertThat(planPolicies).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회에서 제외")
        void findByPlanIdExcludesDeleted() {
            // given
            VacationPolicy policy2 = VacationPolicy.createManualGrantPolicy(
                    "건강휴가", "건강휴가 정책", VacationType.HEALTH, new BigDecimal("3.0"),
                    YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
            );
            em.persist(policy2);

            VacationPlanPolicy activePlanPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.Y);
            VacationPlanPolicy deletedPlanPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy2, 2, YNType.N);
            vacationPlanPolicyRepository.saveAll(List.of(activePlanPolicy, deletedPlanPolicy));
            deletedPlanPolicy.deletePlanPolicy();
            em.flush();
            em.clear();

            // when
            List<VacationPlanPolicy> planPolicies = vacationPlanPolicyRepository.findByPlanId(plan.getId());

            // then
            assertThat(planPolicies).hasSize(1);
            assertThat(planPolicies.get(0).getVacationPolicy().getName()).isEqualTo("연차");
        }
    }

    @Nested
    @DisplayName("findByPolicyId")
    class FindByPolicyId {
        @Test
        @DisplayName("정책 ID로 매핑 목록 조회 성공")
        void findByPolicyIdSuccess() {
            // given
            VacationPlan plan2 = VacationPlan.createPlan("SENIOR", "선임 플랜", "선임용");
            em.persist(plan2);

            VacationPlanPolicy planPolicy1 = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.Y);
            VacationPlanPolicy planPolicy2 = VacationPlanPolicy.createPlanPolicy(plan2, policy, 1, YNType.N);
            vacationPlanPolicyRepository.saveAll(List.of(planPolicy1, planPolicy2));
            em.flush();
            em.clear();

            // when
            List<VacationPlanPolicy> planPolicies = vacationPlanPolicyRepository.findByPolicyId(policy.getId());

            // then
            assertThat(planPolicies).hasSize(2);
            assertThat(planPolicies).extracting(pp -> pp.getVacationPlan().getCode())
                    .containsExactlyInAnyOrder("DEFAULT", "SENIOR");
        }

        @Test
        @DisplayName("정책이 매핑된 플랜이 없으면 빈 리스트 반환")
        void findByPolicyIdEmpty() {
            // given
            em.flush();
            em.clear();

            // when
            List<VacationPlanPolicy> planPolicies = vacationPlanPolicyRepository.findByPolicyId(policy.getId());

            // then
            assertThat(planPolicies).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회에서 제외")
        void findByPolicyIdExcludesDeleted() {
            // given
            VacationPlan plan2 = VacationPlan.createPlan("SENIOR", "선임 플랜", "선임용");
            em.persist(plan2);

            VacationPlanPolicy activePlanPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.Y);
            VacationPlanPolicy deletedPlanPolicy = VacationPlanPolicy.createPlanPolicy(plan2, policy, 1, YNType.N);
            vacationPlanPolicyRepository.saveAll(List.of(activePlanPolicy, deletedPlanPolicy));
            deletedPlanPolicy.deletePlanPolicy();
            em.flush();
            em.clear();

            // when
            List<VacationPlanPolicy> planPolicies = vacationPlanPolicyRepository.findByPolicyId(policy.getId());

            // then
            assertThat(planPolicies).hasSize(1);
            assertThat(planPolicies.get(0).getVacationPlan().getCode()).isEqualTo("DEFAULT");
        }
    }

    @Nested
    @DisplayName("findByPlanIdAndPolicyId")
    class FindByPlanIdAndPolicyId {
        @Test
        @DisplayName("플랜 ID와 정책 ID로 매핑 조회 성공")
        void findByPlanIdAndPolicyIdSuccess() {
            // given
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.N);
            vacationPlanPolicyRepository.save(planPolicy);
            em.flush();
            em.clear();

            // when
            Optional<VacationPlanPolicy> findPlanPolicy = vacationPlanPolicyRepository.findByPlanIdAndPolicyId(
                    plan.getId(), policy.getId());

            // then
            assertThat(findPlanPolicy).isPresent();
            assertThat(findPlanPolicy.get().getSortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("매핑이 없으면 빈 Optional 반환")
        void findByPlanIdAndPolicyIdNotFound() {
            // given
            em.flush();
            em.clear();

            // when
            Optional<VacationPlanPolicy> findPlanPolicy = vacationPlanPolicyRepository.findByPlanIdAndPolicyId(
                    plan.getId(), policy.getId());

            // then
            assertThat(findPlanPolicy).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회되지 않음")
        void findByPlanIdAndPolicyIdDeletedPlanPolicy() {
            // given
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.N);
            vacationPlanPolicyRepository.save(planPolicy);
            planPolicy.deletePlanPolicy();
            em.flush();
            em.clear();

            // when
            Optional<VacationPlanPolicy> findPlanPolicy = vacationPlanPolicyRepository.findByPlanIdAndPolicyId(
                    plan.getId(), policy.getId());

            // then
            assertThat(findPlanPolicy).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByPlanIdAndPolicyId")
    class ExistsByPlanIdAndPolicyId {
        @Test
        @DisplayName("매핑 존재 여부 확인 - 존재함")
        void existsByPlanIdAndPolicyIdTrue() {
            // given
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.N);
            vacationPlanPolicyRepository.save(planPolicy);
            em.flush();
            em.clear();

            // when
            boolean exists = vacationPlanPolicyRepository.existsByPlanIdAndPolicyId(plan.getId(), policy.getId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("매핑 존재 여부 확인 - 없음")
        void existsByPlanIdAndPolicyIdFalse() {
            // given
            em.flush();
            em.clear();

            // when
            boolean exists = vacationPlanPolicyRepository.existsByPlanIdAndPolicyId(plan.getId(), policy.getId());

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 매핑은 존재하지 않음으로 처리")
        void existsByPlanIdAndPolicyIdDeletedPlanPolicy() {
            // given
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.N);
            vacationPlanPolicyRepository.save(planPolicy);
            planPolicy.deletePlanPolicy();
            em.flush();
            em.clear();

            // when
            boolean exists = vacationPlanPolicyRepository.existsByPlanIdAndPolicyId(plan.getId(), policy.getId());

            // then
            assertThat(exists).isFalse();
        }
    }
}
