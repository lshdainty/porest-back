package com.lshdainty.porest.repository;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.work.domain.WorkCode;
import com.lshdainty.porest.work.domain.WorkHistory;
import com.lshdainty.porest.work.repository.WorkHistoryCustomRepositoryImpl;
import com.lshdainty.porest.work.type.CodeType;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({WorkHistoryCustomRepositoryImpl.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPA 업무이력 레포지토리 테스트")
class WorkHistoryRepositoryImplTest {
    @Autowired
    private WorkHistoryCustomRepositoryImpl workHistoryRepository;

    @Autowired
    private TestEntityManager em;

    private User user;
    private WorkCode group;
    private WorkCode part;
    private WorkCode division;

    @BeforeEach
    void setUp() {
        user = User.createUser("user1");
        em.persist(user);

        group = WorkCode.createWorkCode("DEV", "개발", CodeType.LABEL, null, 1);
        em.persist(group);

        part = WorkCode.createWorkCode("BACKEND", "백엔드", CodeType.LABEL, group, 1);
        em.persist(part);

        division = WorkCode.createWorkCode("API", "API 개발", CodeType.OPTION, part, 1);
        em.persist(division);
    }

    @Test
    @DisplayName("업무이력 저장 및 단건 조회")
    void save() {
        // given
        LocalDate date = LocalDate.of(2025, 1, 15);
        BigDecimal hours = new BigDecimal("8.0");
        String content = "API 개발 업무 진행";

        WorkHistory workHistory = WorkHistory.createWorkHistory(date, user, group, part, division, hours, content);

        // when
        workHistoryRepository.save(workHistory);
        em.flush();
        em.clear();

        // then
        Optional<WorkHistory> findHistory = workHistoryRepository.findById(workHistory.getSeq());
        assertThat(findHistory.isPresent()).isTrue();
        assertThat(findHistory.get().getDate()).isEqualTo(date);
        assertThat(findHistory.get().getHours()).isEqualByComparingTo(hours);
        assertThat(findHistory.get().getContent()).isEqualTo(content);
        assertThat(findHistory.get().getUser().getId()).isEqualTo("user1");
        assertThat(findHistory.get().getGroup().getCode()).isEqualTo("DEV");
        assertThat(findHistory.get().getPart().getCode()).isEqualTo("BACKEND");
        assertThat(findHistory.get().getDivision().getCode()).isEqualTo("API");
    }

    @Test
    @DisplayName("단건 조회 시 업무이력이 없어도 Null이 반환되면 안된다.")
    void findByIdEmpty() {
        // given
        Long historyId = 999L;

        // when
        Optional<WorkHistory> findHistory = workHistoryRepository.findById(historyId);

        // then
        assertThat(findHistory.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("전체 업무이력 조회")
    void findAll() {
        // given
        WorkHistory history1 = WorkHistory.createWorkHistory(
                LocalDate.of(2025, 1, 15), user, group, part, division, new BigDecimal("8.0"), "업무1"
        );
        WorkHistory history2 = WorkHistory.createWorkHistory(
                LocalDate.of(2025, 1, 16), user, group, part, division, new BigDecimal("4.0"), "업무2"
        );
        workHistoryRepository.save(history1);
        workHistoryRepository.save(history2);

        em.flush();
        em.clear();

        // when
        List<WorkHistory> histories = workHistoryRepository.findAll();

        // then
        assertThat(histories).hasSize(2);
        // 날짜 역순 정렬
        assertThat(histories.get(0).getDate()).isEqualTo(LocalDate.of(2025, 1, 16));
        assertThat(histories.get(1).getDate()).isEqualTo(LocalDate.of(2025, 1, 15));
    }

    @Test
    @DisplayName("전체 업무이력이 없어도 Null이 반환되면 안된다.")
    void findAllEmpty() {
        // given & when
        List<WorkHistory> histories = workHistoryRepository.findAll();

        // then
        assertThat(histories.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("업무이력 수정")
    void updateWorkHistory() {
        // given
        WorkHistory history = WorkHistory.createWorkHistory(
                LocalDate.of(2025, 1, 15), user, group, part, division, new BigDecimal("8.0"), "기존 업무"
        );
        workHistoryRepository.save(history);
        em.flush();
        em.clear();

        // when
        WorkHistory findHistory = workHistoryRepository.findById(history.getSeq()).orElseThrow();
        findHistory.updateWorkHistory(
                LocalDate.of(2025, 1, 16), null, null, null, null, new BigDecimal("4.0"), "수정된 업무"
        );
        em.flush();
        em.clear();

        // then
        WorkHistory updatedHistory = workHistoryRepository.findById(history.getSeq()).orElseThrow();
        assertThat(updatedHistory.getDate()).isEqualTo(LocalDate.of(2025, 1, 16));
        assertThat(updatedHistory.getHours()).isEqualByComparingTo(new BigDecimal("4.0"));
        assertThat(updatedHistory.getContent()).isEqualTo("수정된 업무");
    }

    @Test
    @DisplayName("여러 유저의 업무이력 조회")
    void findAllMultipleUsers() {
        // given
        User user2 = User.createUser("user2");
        em.persist(user2);

        WorkHistory history1 = WorkHistory.createWorkHistory(
                LocalDate.of(2025, 1, 15), user, group, part, division, new BigDecimal("8.0"), "user1 업무"
        );
        WorkHistory history2 = WorkHistory.createWorkHistory(
                LocalDate.of(2025, 1, 15), user2, group, part, division, new BigDecimal("8.0"), "user2 업무"
        );
        workHistoryRepository.save(history1);
        workHistoryRepository.save(history2);

        em.flush();
        em.clear();

        // when
        List<WorkHistory> histories = workHistoryRepository.findAll();

        // then
        assertThat(histories).hasSize(2);
        assertThat(histories).extracting("content").containsExactlyInAnyOrder("user1 업무", "user2 업무");
    }

    @Test
    @DisplayName("업무이력 조회 시 연관 엔티티 함께 조회")
    void findByIdWithRelations() {
        // given
        WorkHistory history = WorkHistory.createWorkHistory(
                LocalDate.of(2025, 1, 15), user, group, part, division, new BigDecimal("8.0"), "업무"
        );
        workHistoryRepository.save(history);
        em.flush();
        em.clear();

        // when
        Optional<WorkHistory> findHistory = workHistoryRepository.findById(history.getSeq());

        // then
        assertThat(findHistory.isPresent()).isTrue();
        // fetch join으로 연관 엔티티도 함께 로드됨
        assertThat(findHistory.get().getUser()).isNotNull();
        assertThat(findHistory.get().getGroup()).isNotNull();
        assertThat(findHistory.get().getPart()).isNotNull();
        assertThat(findHistory.get().getDivision()).isNotNull();
    }
}
