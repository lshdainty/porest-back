package com.lshdainty.porest.repository;

import com.lshdainty.porest.common.type.CountryCode;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.company.type.OriginCompanyType;
import com.lshdainty.porest.vacation.domain.UserVacationPlan;
import com.lshdainty.porest.vacation.domain.VacationPlan;
import com.lshdainty.porest.vacation.domain.VacationPlanPolicy;
import com.lshdainty.porest.vacation.domain.VacationPolicy;
import com.lshdainty.porest.vacation.repository.UserVacationPlanJpaRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UserVacationPlanJpaRepository.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPQL 사용자-휴가 플랜 매핑 레포지토리 테스트")
class UserVacationPlanJpaRepositoryTest {
    @Autowired
    private UserVacationPlanJpaRepository userVacationPlanRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private VacationPlan plan;

    @BeforeEach
    void setUp() {
        user = User.createUser(
                "user1", "password", "테스트유저1", "user1@test.com",
                LocalDate.of(1990, 1, 1), OriginCompanyType.DTOL, "9 ~ 18",
                YNType.N, null, null, CountryCode.KR
        );
        plan = VacationPlan.createPlan("DEFAULT", "기본 플랜", "기본 휴가 플랜");
        em.persist(user);
        em.persist(plan);
    }

    @Nested
    @DisplayName("save")
    class Save {
        @Test
        @DisplayName("사용자-플랜 매핑 저장 성공")
        void saveSuccess() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);

            // when
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // then
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findById(userVacationPlan.getId());
            assertThat(findUserVacationPlan).isPresent();
            assertThat(findUserVacationPlan.get().getVacationPlan().getCode()).isEqualTo("DEFAULT");
        }
    }

    @Nested
    @DisplayName("saveAll")
    class SaveAll {
        @Test
        @DisplayName("여러 사용자-플랜 매핑 일괄 저장 성공")
        void saveAllSuccess() {
            // given
            VacationPlan plan2 = VacationPlan.createPlan("SENIOR", "선임 플랜", "선임용 플랜");
            em.persist(plan2);

            UserVacationPlan userVacationPlan1 = UserVacationPlan.createUserVacationPlan(user, plan);
            UserVacationPlan userVacationPlan2 = UserVacationPlan.createUserVacationPlan(user, plan2);

            // when
            userVacationPlanRepository.saveAll(List.of(userVacationPlan1, userVacationPlan2));
            em.flush();
            em.clear();

            // then
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserId(user.getId());
            assertThat(userVacationPlans).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("ID로 사용자-플랜 매핑 조회 성공")
        void findByIdSuccess() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findById(userVacationPlan.getId());

            // then
            assertThat(findUserVacationPlan).isPresent();
            assertThat(findUserVacationPlan.get().getVacationPlan().getCode()).isEqualTo("DEFAULT");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void findByIdNotFound() {
            // given & when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findById(999L);

            // then
            assertThat(findUserVacationPlan).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회되지 않음")
        void findByIdDeletedUserVacationPlan() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            userVacationPlan.deleteUserVacationPlan();
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findById(userVacationPlan.getId());

            // then
            assertThat(findUserVacationPlan).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {
        @Test
        @DisplayName("사용자 ID로 매핑 목록 조회 성공")
        void findByUserIdSuccess() {
            // given
            VacationPlan plan2 = VacationPlan.createPlan("SENIOR", "선임 플랜", "선임용 플랜");
            em.persist(plan2);

            UserVacationPlan userVacationPlan1 = UserVacationPlan.createUserVacationPlan(user, plan);
            UserVacationPlan userVacationPlan2 = UserVacationPlan.createUserVacationPlan(user, plan2);
            userVacationPlanRepository.saveAll(List.of(userVacationPlan1, userVacationPlan2));
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserId(user.getId());

            // then
            assertThat(userVacationPlans).hasSize(2);
        }

        @Test
        @DisplayName("사용자에게 할당된 플랜이 없으면 빈 리스트 반환")
        void findByUserIdEmpty() {
            // given
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserId(user.getId());

            // then
            assertThat(userVacationPlans).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회에서 제외")
        void findByUserIdExcludesDeleted() {
            // given
            VacationPlan plan2 = VacationPlan.createPlan("SENIOR", "선임 플랜", "선임용 플랜");
            em.persist(plan2);

            UserVacationPlan activeUserVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            UserVacationPlan deletedUserVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan2);
            userVacationPlanRepository.saveAll(List.of(activeUserVacationPlan, deletedUserVacationPlan));
            deletedUserVacationPlan.deleteUserVacationPlan();
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserId(user.getId());

            // then
            assertThat(userVacationPlans).hasSize(1);
            assertThat(userVacationPlans.get(0).getVacationPlan().getCode()).isEqualTo("DEFAULT");
        }
    }

    @Nested
    @DisplayName("findByUserIdWithPlan")
    class FindByUserIdWithPlan {
        @Test
        @DisplayName("사용자 ID로 매핑 조회 시 플랜도 함께 조회")
        void findByUserIdWithPlanSuccess() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserIdWithPlan(user.getId());

            // then
            assertThat(userVacationPlans).hasSize(1);
            assertThat(userVacationPlans.get(0).getVacationPlan().getCode()).isEqualTo("DEFAULT");
        }

        @Test
        @DisplayName("사용자에게 할당된 플랜이 없으면 빈 리스트 반환")
        void findByUserIdWithPlanEmpty() {
            // given
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserIdWithPlan(user.getId());

            // then
            assertThat(userVacationPlans).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUserIdWithPlanAndPolicies")
    class FindByUserIdWithPlanAndPolicies {
        @Test
        @DisplayName("사용자 ID로 매핑 조회 시 플랜과 정책도 함께 조회")
        void findByUserIdWithPlanAndPoliciesSuccess() {
            // given
            VacationPolicy policy = VacationPolicy.createManualGrantPolicy(
                    "연차", "연차 정책", VacationType.ANNUAL, new BigDecimal("15.0"),
                    YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
            );
            em.persist(policy);
            VacationPlanPolicy planPolicy = VacationPlanPolicy.createPlanPolicy(plan, policy, 1, YNType.Y);
            em.persist(planPolicy);
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserIdWithPlanAndPolicies(user.getId());

            // then
            assertThat(userVacationPlans).hasSize(1);
            assertThat(userVacationPlans.get(0).getVacationPlan().getVacationPlanPolicies()).hasSize(1);
        }

        @Test
        @DisplayName("사용자에게 할당된 플랜이 없으면 빈 리스트 반환")
        void findByUserIdWithPlanAndPoliciesEmpty() {
            // given
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByUserIdWithPlanAndPolicies(user.getId());

            // then
            assertThat(userVacationPlans).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByPlanId")
    class FindByPlanId {
        @Test
        @DisplayName("플랜 ID로 매핑 목록 조회 성공")
        void findByPlanIdSuccess() {
            // given
            User user2 = User.createUser(
                    "user2", "password", "테스트유저2", "user2@test.com",
                    LocalDate.of(1991, 1, 1), OriginCompanyType.DTOL, "9 ~ 18",
                    YNType.N, null, null, CountryCode.KR
            );
            em.persist(user2);

            UserVacationPlan userVacationPlan1 = UserVacationPlan.createUserVacationPlan(user, plan);
            UserVacationPlan userVacationPlan2 = UserVacationPlan.createUserVacationPlan(user2, plan);
            userVacationPlanRepository.saveAll(List.of(userVacationPlan1, userVacationPlan2));
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByPlanId(plan.getId());

            // then
            assertThat(userVacationPlans).hasSize(2);
        }

        @Test
        @DisplayName("플랜에 할당된 사용자가 없으면 빈 리스트 반환")
        void findByPlanIdEmpty() {
            // given
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByPlanId(plan.getId());

            // then
            assertThat(userVacationPlans).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회에서 제외")
        void findByPlanIdExcludesDeleted() {
            // given
            User user2 = User.createUser(
                    "user2", "password", "테스트유저2", "user2@test.com",
                    LocalDate.of(1991, 1, 1), OriginCompanyType.DTOL, "9 ~ 18",
                    YNType.N, null, null, CountryCode.KR
            );
            em.persist(user2);

            UserVacationPlan activeUserVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            UserVacationPlan deletedUserVacationPlan = UserVacationPlan.createUserVacationPlan(user2, plan);
            userVacationPlanRepository.saveAll(List.of(activeUserVacationPlan, deletedUserVacationPlan));
            deletedUserVacationPlan.deleteUserVacationPlan();
            em.flush();
            em.clear();

            // when
            List<UserVacationPlan> userVacationPlans = userVacationPlanRepository.findByPlanId(plan.getId());

            // then
            assertThat(userVacationPlans).hasSize(1);
            assertThat(userVacationPlans.get(0).getUser().getId()).isEqualTo(user.getId());
        }
    }

    @Nested
    @DisplayName("findByUserIdAndPlanId")
    class FindByUserIdAndPlanId {
        @Test
        @DisplayName("사용자 ID와 플랜 ID로 매핑 조회 성공")
        void findByUserIdAndPlanIdSuccess() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findByUserIdAndPlanId(
                    user.getId(), plan.getId());

            // then
            assertThat(findUserVacationPlan).isPresent();
            assertThat(findUserVacationPlan.get().getVacationPlan().getCode()).isEqualTo("DEFAULT");
        }

        @Test
        @DisplayName("매핑이 없으면 빈 Optional 반환")
        void findByUserIdAndPlanIdNotFound() {
            // given
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findByUserIdAndPlanId(
                    user.getId(), plan.getId());

            // then
            assertThat(findUserVacationPlan).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회되지 않음")
        void findByUserIdAndPlanIdDeletedUserVacationPlan() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            userVacationPlan.deleteUserVacationPlan();
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findByUserIdAndPlanId(
                    user.getId(), plan.getId());

            // then
            assertThat(findUserVacationPlan).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUserIdAndPlanCode")
    class FindByUserIdAndPlanCode {
        @Test
        @DisplayName("사용자 ID와 플랜 코드로 매핑 조회 성공")
        void findByUserIdAndPlanCodeSuccess() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findByUserIdAndPlanCode(
                    user.getId(), "DEFAULT");

            // then
            assertThat(findUserVacationPlan).isPresent();
            assertThat(findUserVacationPlan.get().getVacationPlan().getName()).isEqualTo("기본 플랜");
        }

        @Test
        @DisplayName("매핑이 없으면 빈 Optional 반환")
        void findByUserIdAndPlanCodeNotFound() {
            // given
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findByUserIdAndPlanCode(
                    user.getId(), "DEFAULT");

            // then
            assertThat(findUserVacationPlan).isEmpty();
        }

        @Test
        @DisplayName("삭제된 매핑은 조회되지 않음")
        void findByUserIdAndPlanCodeDeletedUserVacationPlan() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            userVacationPlan.deleteUserVacationPlan();
            em.flush();
            em.clear();

            // when
            Optional<UserVacationPlan> findUserVacationPlan = userVacationPlanRepository.findByUserIdAndPlanCode(
                    user.getId(), "DEFAULT");

            // then
            assertThat(findUserVacationPlan).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserIdAndPlanId")
    class ExistsByUserIdAndPlanId {
        @Test
        @DisplayName("매핑 존재 여부 확인 - 존재함")
        void existsByUserIdAndPlanIdTrue() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // when
            boolean exists = userVacationPlanRepository.existsByUserIdAndPlanId(user.getId(), plan.getId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("매핑 존재 여부 확인 - 없음")
        void existsByUserIdAndPlanIdFalse() {
            // given
            em.flush();
            em.clear();

            // when
            boolean exists = userVacationPlanRepository.existsByUserIdAndPlanId(user.getId(), plan.getId());

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 매핑은 존재하지 않음으로 처리")
        void existsByUserIdAndPlanIdDeletedUserVacationPlan() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            userVacationPlan.deleteUserVacationPlan();
            em.flush();
            em.clear();

            // when
            boolean exists = userVacationPlanRepository.existsByUserIdAndPlanId(user.getId(), plan.getId());

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByUserIdAndPlanCode")
    class ExistsByUserIdAndPlanCode {
        @Test
        @DisplayName("매핑 존재 여부 확인 - 존재함")
        void existsByUserIdAndPlanCodeTrue() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            em.flush();
            em.clear();

            // when
            boolean exists = userVacationPlanRepository.existsByUserIdAndPlanCode(user.getId(), "DEFAULT");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("매핑 존재 여부 확인 - 없음")
        void existsByUserIdAndPlanCodeFalse() {
            // given
            em.flush();
            em.clear();

            // when
            boolean exists = userVacationPlanRepository.existsByUserIdAndPlanCode(user.getId(), "DEFAULT");

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 매핑은 존재하지 않음으로 처리")
        void existsByUserIdAndPlanCodeDeletedUserVacationPlan() {
            // given
            UserVacationPlan userVacationPlan = UserVacationPlan.createUserVacationPlan(user, plan);
            userVacationPlanRepository.save(userVacationPlan);
            userVacationPlan.deleteUserVacationPlan();
            em.flush();
            em.clear();

            // when
            boolean exists = userVacationPlanRepository.existsByUserIdAndPlanCode(user.getId(), "DEFAULT");

            // then
            assertThat(exists).isFalse();
        }
    }
}
