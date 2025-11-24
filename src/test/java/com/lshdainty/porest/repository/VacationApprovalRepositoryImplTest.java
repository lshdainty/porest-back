package com.lshdainty.porest.repository;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.vacation.domain.VacationApproval;
import com.lshdainty.porest.vacation.domain.VacationGrant;
import com.lshdainty.porest.vacation.domain.VacationPolicy;
import com.lshdainty.porest.vacation.repository.VacationApprovalCustomRepositoryImpl;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({VacationApprovalCustomRepositoryImpl.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPA 휴가승인 레포지토리 테스트")
class VacationApprovalRepositoryImplTest {
    @Autowired
    private VacationApprovalCustomRepositoryImpl vacationApprovalRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private User approver;
    private VacationPolicy policy;
    private VacationGrant grant;

    @BeforeEach
    void setUp() {
        user = User.createUser("user1");
        em.persist(user);

        approver = User.createUser("approver1");
        em.persist(approver);

        policy = VacationPolicy.createOnRequestPolicy(
                "연차", "연차 정책", VacationType.ANNUAL, new BigDecimal("1.0"),
                YNType.N, YNType.N, 1, EffectiveType.IMMEDIATELY, ExpirationType.END_OF_YEAR
        );
        em.persist(policy);

        grant = VacationGrant.createPendingVacationGrant(
                user, policy, "연차 신청", VacationType.ANNUAL, new BigDecimal("1.0"),
                LocalDateTime.of(2025, 6, 1, 9, 0), LocalDateTime.of(2025, 6, 1, 18, 0), "개인 사유"
        );
        em.persist(grant);
    }

    @Test
    @DisplayName("승인 저장")
    void save() {
        // given
        VacationApproval approval = VacationApproval.createVacationApproval(grant, approver, 1);

        // when
        vacationApprovalRepository.save(approval);
        em.flush();
        em.clear();

        // then
        Optional<VacationApproval> result = vacationApprovalRepository.findById(approval.getId());
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("승인 다건 저장")
    void saveAll() {
        // given
        User approver2 = User.createUser("approver2");
        em.persist(approver2);

        List<VacationApproval> approvals = List.of(
                VacationApproval.createVacationApproval(grant, approver, 1),
                VacationApproval.createVacationApproval(grant, approver2, 2)
        );

        // when
        vacationApprovalRepository.saveAll(approvals);
        em.flush();
        em.clear();

        // then
        List<VacationApproval> result = vacationApprovalRepository.findByVacationGrantId(grant.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("부여 ID로 승인 목록 조회")
    void findByVacationGrantId() {
        // given
        vacationApprovalRepository.save(VacationApproval.createVacationApproval(grant, approver, 1));
        em.flush();
        em.clear();

        // when
        List<VacationApproval> result = vacationApprovalRepository.findByVacationGrantId(grant.getId());

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("승인자 ID로 부여 ID 목록 조회")
    void findAllVacationGrantIdsByApproverId() {
        // given
        vacationApprovalRepository.save(VacationApproval.createVacationApproval(grant, approver, 1));
        em.flush();
        em.clear();

        // when
        List<Long> result = vacationApprovalRepository.findAllVacationGrantIdsByApproverId("approver1");

        // then
        assertThat(result).hasSize(1);
        assertThat(result).contains(grant.getId());
    }

    @Test
    @DisplayName("ID로 승인 조회")
    void findById() {
        // given
        VacationApproval approval = VacationApproval.createVacationApproval(grant, approver, 1);
        vacationApprovalRepository.save(approval);
        em.flush();
        em.clear();

        // when
        Optional<VacationApproval> result = vacationApprovalRepository.findById(approval.getId());

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("ID로 승인 조회 시 부여와 유저 fetch join")
    void findByIdWithVacationGrantAndUser() {
        // given
        VacationApproval approval = VacationApproval.createVacationApproval(grant, approver, 1);
        vacationApprovalRepository.save(approval);
        em.flush();
        em.clear();

        // when
        Optional<VacationApproval> result = vacationApprovalRepository
                .findByIdWithVacationGrantAndUser(approval.getId());

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getVacationGrant()).isNotNull();
        assertThat(result.get().getApprover()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 빈 Optional 반환")
    void findByIdEmpty() {
        // when
        Optional<VacationApproval> result = vacationApprovalRepository.findById(999L);

        // then
        assertThat(result.isEmpty()).isTrue();
    }
}
