package com.lshdainty.porest.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.vacation.domain.UserVacationPolicy;
import com.lshdainty.porest.vacation.domain.VacationPolicy;
import com.lshdainty.porest.vacation.repository.UserVacationPolicyCustomRepositoryImpl;
import com.lshdainty.porest.vacation.type.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({UserVacationPolicyCustomRepositoryImpl.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPA 유저휴가정책 레포지토리 테스트")
class UserVacationPolicyRepositoryImplTest {
    @Autowired
    private UserVacationPolicyCustomRepositoryImpl userVacationPolicyRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private VacationPolicy policy;

    @BeforeEach
    void setUp() {
        user = User.createUser("user1");
        em.persist(user);

        policy = VacationPolicy.createManualGrantPolicy(
                "연차", "연차 정책", VacationType.ANNUAL, new BigDecimal("15.0"),
                YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
        );
        em.persist(policy);
    }

    @Test
    @DisplayName("유저휴가정책 저장")
    void save() {
        // given
        UserVacationPolicy uvp = UserVacationPolicy.createUserVacationPolicy(user, policy);

        // when
        userVacationPolicyRepository.save(uvp);
        em.flush();
        em.clear();

        // then
        Optional<UserVacationPolicy> result = userVacationPolicyRepository.findById(uvp.getId());
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("유저휴가정책 다건 저장")
    void saveAll() {
        // given
        VacationPolicy policy2 = VacationPolicy.createManualGrantPolicy(
                "경조", "경조 정책", VacationType.BEREAVEMENT, new BigDecimal("5.0"),
                YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.ONE_MONTHS_AFTER_GRANT
        );
        em.persist(policy2);

        List<UserVacationPolicy> uvps = List.of(
                UserVacationPolicy.createUserVacationPolicy(user, policy),
                UserVacationPolicy.createUserVacationPolicy(user, policy2)
        );

        // when
        userVacationPolicyRepository.saveAll(uvps);
        em.flush();
        em.clear();

        // then
        List<UserVacationPolicy> result = userVacationPolicyRepository.findByUserId("user1");
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("유저 ID로 유저휴가정책 조회")
    void findByUserId() {
        // given
        userVacationPolicyRepository.save(UserVacationPolicy.createUserVacationPolicy(user, policy));
        em.flush();
        em.clear();

        // when
        List<UserVacationPolicy> result = userVacationPolicyRepository.findByUserId("user1");

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("유저와 정책으로 존재 여부 확인")
    void existsByUserIdAndVacationPolicyId() {
        // given
        userVacationPolicyRepository.save(UserVacationPolicy.createUserVacationPolicy(user, policy));
        em.flush();
        em.clear();

        // when
        boolean exists = userVacationPolicyRepository.existsByUserIdAndVacationPolicyId("user1", policy.getId());
        boolean notExists = userVacationPolicyRepository.existsByUserIdAndVacationPolicyId("user1", 999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("ID로 유저휴가정책 조회")
    void findById() {
        // given
        UserVacationPolicy uvp = UserVacationPolicy.createUserVacationPolicy(user, policy);
        userVacationPolicyRepository.save(uvp);
        em.flush();
        em.clear();

        // when
        Optional<UserVacationPolicy> result = userVacationPolicyRepository.findById(uvp.getId());

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("유저와 정책 ID로 유저휴가정책 조회")
    void findByUserIdAndVacationPolicyId() {
        // given
        userVacationPolicyRepository.save(UserVacationPolicy.createUserVacationPolicy(user, policy));
        em.flush();
        em.clear();

        // when
        Optional<UserVacationPolicy> result = userVacationPolicyRepository
                .findByUserIdAndVacationPolicyId("user1", policy.getId());

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("정책 ID로 유저휴가정책 목록 조회")
    void findByVacationPolicyId() {
        // given
        User user2 = User.createUser("user2");
        em.persist(user2);

        userVacationPolicyRepository.save(UserVacationPolicy.createUserVacationPolicy(user, policy));
        userVacationPolicyRepository.save(UserVacationPolicy.createUserVacationPolicy(user2, policy));
        em.flush();
        em.clear();

        // when
        List<UserVacationPolicy> result = userVacationPolicyRepository.findByVacationPolicyId(policy.getId());

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("오늘 부여 대상 반복 정책 조회")
    void findRepeatGrantTargetsForToday() {
        // given
        VacationPolicy repeatPolicy = VacationPolicy.createRepeatGrantPolicy(
                "반복연차", "반복 정책", VacationType.ANNUAL,
                new BigDecimal("1.0"), YNType.N, RepeatUnit.YEARLY, 1, null, null,
                LocalDateTime.of(2025, 1, 1, 0, 0), YNType.Y, null,
                EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
        );
        em.persist(repeatPolicy);

        UserVacationPolicy uvp = UserVacationPolicy.createUserVacationPolicy(user, repeatPolicy);
        userVacationPolicyRepository.save(uvp);
        em.flush();
        em.clear();

        // when
        List<UserVacationPolicy> result = userVacationPolicyRepository
                .findRepeatGrantTargetsForToday(LocalDate.of(2025, 1, 1));

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("필터 조건으로 유저휴가정책 조회")
    void findByUserIdWithFilters() {
        // given
        userVacationPolicyRepository.save(UserVacationPolicy.createUserVacationPolicy(user, policy));
        em.flush();
        em.clear();

        // when
        List<UserVacationPolicy> result = userVacationPolicyRepository
                .findByUserIdWithFilters("user1", VacationType.ANNUAL, GrantMethod.MANUAL_GRANT);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("필터 조건이 null이면 전체 조회")
    void findByUserIdWithFiltersNullFilter() {
        // given
        userVacationPolicyRepository.save(UserVacationPolicy.createUserVacationPolicy(user, policy));
        em.flush();
        em.clear();

        // when
        List<UserVacationPolicy> result = userVacationPolicyRepository
                .findByUserIdWithFilters("user1", null, null);

        // then
        assertThat(result).hasSize(1);
    }
}
