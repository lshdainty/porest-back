package com.lshdainty.porest.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.vacation.domain.VacationGrant;
import com.lshdainty.porest.vacation.domain.VacationPolicy;
import com.lshdainty.porest.vacation.domain.VacationUsage;
import com.lshdainty.porest.vacation.domain.VacationUsageDeduction;
import com.lshdainty.porest.vacation.repository.VacationUsageDeductionCustomRepositoryImpl;
import com.lshdainty.porest.vacation.type.EffectiveType;
import com.lshdainty.porest.vacation.type.ExpirationType;
import com.lshdainty.porest.vacation.type.VacationTimeType;
import com.lshdainty.porest.vacation.type.VacationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({VacationUsageDeductionCustomRepositoryImpl.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPA 휴가사용차감 레포지토리 테스트")
class VacationUsageDeductionRepositoryImplTest {
    @Autowired
    private VacationUsageDeductionCustomRepositoryImpl deductionRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private VacationPolicy policy;
    private VacationGrant grant;
    private VacationUsage usage;

    @BeforeEach
    void setUp() {
        user = User.createUser("user1");
        em.persist(user);

        policy = VacationPolicy.createManualGrantPolicy(
                "연차", "연차 정책", VacationType.ANNUAL, new BigDecimal("15.0"),
                YNType.N, YNType.N, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
        );
        em.persist(policy);

        grant = VacationGrant.createVacationGrant(
                user, policy, "2025년 연차", VacationType.ANNUAL, new BigDecimal("15.0"),
                LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        em.persist(grant);

        usage = VacationUsage.createVacationUsage(
                user, "연차 사용", VacationTimeType.DAYOFF,
                LocalDateTime.of(2025, 6, 1, 9, 0), LocalDateTime.of(2025, 6, 1, 18, 0),
                new BigDecimal("1.0000")
        );
        em.persist(usage);
    }

    @Test
    @DisplayName("차감 저장")
    void save() {
        // given
        VacationUsageDeduction deduction = VacationUsageDeduction.createVacationUsageDeduction(
                usage, grant, new BigDecimal("1.0000")
        );

        // when
        deductionRepository.save(deduction);
        em.flush();
        em.clear();

        // then
        List<VacationUsageDeduction> result = deductionRepository.findByUsageId(usage.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeductedTime()).isEqualByComparingTo(new BigDecimal("1.0000"));
    }

    @Test
    @DisplayName("차감 다건 저장")
    void saveAll() {
        // given
        VacationGrant grant2 = VacationGrant.createVacationGrant(
                user, policy, "추가 연차", VacationType.ANNUAL, new BigDecimal("5.0"),
                LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 12, 31, 23, 59)
        );
        em.persist(grant2);

        List<VacationUsageDeduction> deductions = List.of(
                VacationUsageDeduction.createVacationUsageDeduction(usage, grant, new BigDecimal("0.5")),
                VacationUsageDeduction.createVacationUsageDeduction(usage, grant2, new BigDecimal("0.5"))
        );

        // when
        deductionRepository.saveAll(deductions);
        em.flush();
        em.clear();

        // then
        List<VacationUsageDeduction> result = deductionRepository.findByUsageId(usage.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("사용 ID로 차감 조회")
    void findByUsageId() {
        // given
        deductionRepository.save(VacationUsageDeduction.createVacationUsageDeduction(
                usage, grant, new BigDecimal("1.0000")
        ));
        em.flush();
        em.clear();

        // when
        List<VacationUsageDeduction> result = deductionRepository.findByUsageId(usage.getId());

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("부여 ID로 차감 조회")
    void findByGrantId() {
        // given
        deductionRepository.save(VacationUsageDeduction.createVacationUsageDeduction(
                usage, grant, new BigDecimal("1.0000")
        ));
        em.flush();
        em.clear();

        // when
        List<VacationUsageDeduction> result = deductionRepository.findByGrantId(grant.getId());

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("사용 ID로 조회 시 없으면 빈 리스트 반환")
    void findByUsageIdEmpty() {
        // when
        List<VacationUsageDeduction> result = deductionRepository.findByUsageId(999L);

        // then
        assertThat(result).isEmpty();
    }
}
