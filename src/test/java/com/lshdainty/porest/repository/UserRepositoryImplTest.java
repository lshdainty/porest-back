package com.lshdainty.porest.repository;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.user.repository.UserRepositoryImpl;
import com.lshdainty.porest.company.type.OriginCompanyType;
import com.lshdainty.porest.common.type.YNType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@Import({UserRepositoryImpl.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPA 유저 레포지토리 테스트")
class UserRepositoryImplTest {
    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("유저 등록 및 단건 조회")
    void save() {
        // given
        String id = "user1";
        String name = "홍길동";
        String pwd = "";
        String email = "";
        LocalDate birth = LocalDate.of(1970, 2, 4);
        OriginCompanyType company = OriginCompanyType.SKAX;
        String workTime = "9 ~ 6";
        YNType lunarYN = YNType.N;

        User user = User.createUser(id, pwd, name, email, birth, company, workTime, lunarYN, null, null);

        // when
        userRepositoryImpl.save(user);
        em.flush();
        em.clear();

        // then
        Optional<User> findUser = userRepositoryImpl.findById(user.getId());
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get().getId()).isEqualTo(id);
        assertThat(findUser.get().getPwd()).isEqualTo(pwd);
        assertThat(findUser.get().getName()).isEqualTo(name);
        assertThat(findUser.get().getEmail()).isEqualTo(email);
        assertThat(findUser.get().getBirth()).isEqualTo(birth);
        assertThat(findUser.get().getCompany()).isEqualTo(company);
        assertThat(findUser.get().getWorkTime()).isEqualTo(workTime);
        assertThat(findUser.get().getLunarYN()).isEqualTo(lunarYN);
    }

    @Test
    @DisplayName("단건 조회 시 유저가 없어도 Null이 반환되면 안된다.")
    void findByIdEmpty() {
        // given
        String userId = "";

        // when
        Optional<User> findUser = userRepositoryImpl.findById(userId);

        // then
        assertThat(findUser.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("isDeleted가 N인 모든 유저가 보여야한다.")
    void getUsers() {
        // given
        User user1 = User.createUser("user1", "", "", "", LocalDate.of(1990, 1, 1), OriginCompanyType.SKAX, "", YNType.N, null, null);
        User user2 = User.createUser("user2", "", "", "", LocalDate.of(1991, 2, 2), OriginCompanyType.SKAX, "", YNType.N, null, null);
        User user3 = User.createUser("user3", "", "", "", LocalDate.of(1992, 3, 3), OriginCompanyType.SKAX, "", YNType.N, null, null);
        User user4 = User.createUser("user4", "", "", "", LocalDate.of(1993, 4, 4), OriginCompanyType.SKAX, "", YNType.N, null, null);
        List<User> users = List.of(user1, user2, user3, user4);

        for (User user : users) {
            userRepositoryImpl.save(user);
        }

        // when
        user3.deleteUser();
        em.flush();
        em.clear();
        List<User> findUsers = userRepositoryImpl.findUsers();

        // then
        assertThat(findUsers.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("유저가 없어도 Null이 반환되면 안된다.")
    void getUsersEmpty() {
        // given & when
        List<User> users = userRepositoryImpl.findUsers();

        // then
        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("삭제된 유저는 isDeleted 상태값이 Y여야 한다.")
    void deleteUser() {
        // given
        User user = User.createUser("user1", "", "", "", LocalDate.of(1990, 1, 1), OriginCompanyType.SKAX, "", YNType.N, null, null);
        userRepositoryImpl.save(user);

        // when
        user.deleteUser();
        em.flush();
        em.clear();
        Optional<User> findUser = userRepositoryImpl.findById(user.getId());

        // then
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get().getIsDeleted()).isEqualTo(YNType.Y);
    }

    @Test
    @DisplayName("유저 수정")
    void updateUser() {
        // given
        String id = "user1";
        String name = "홍길동";
        String pwd = "";
        String email = "";
        LocalDate birth = LocalDate.of(1970, 2, 4);
        OriginCompanyType company = OriginCompanyType.SKAX;
        String workTime = "9 ~ 6";
        YNType lunarYN = YNType.N;

        User user = User.createUser(id, pwd, name, email, birth, company, workTime, lunarYN, null, null);
        userRepositoryImpl.save(user);

        name = "이서준";
        workTime = "10 ~ 7";

        // when
        user.updateUser(name, null, null, null, null, workTime, null, null, null);
        em.flush();
        em.clear();
        Optional<User> findUser = userRepositoryImpl.findById(user.getId());

        // then
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get().getId()).isEqualTo(id);
        assertThat(findUser.get().getPwd()).isEqualTo(pwd);
        assertThat(findUser.get().getName()).isEqualTo(name);
        assertThat(findUser.get().getEmail()).isEqualTo(email);
        assertThat(findUser.get().getBirth()).isEqualTo(birth);
        assertThat(findUser.get().getCompany()).isEqualTo(company);
        assertThat(findUser.get().getWorkTime()).isEqualTo(workTime);
        assertThat(findUser.get().getLunarYN()).isEqualTo(lunarYN);
    }

    @Test
    @DisplayName("초대 토큰으로 유저 조회")
    void findByInvitationToken() {
        // given
        User user = User.createInvitedUser("user1", "홍길동", "test@example.com",
                OriginCompanyType.SKAX, "9 ~ 6", LocalDate.of(2025, 1, 1));
        userRepositoryImpl.save(user);
        String token = user.getInvitationToken();
        em.flush();
        em.clear();

        // when
        Optional<User> findUser = userRepositoryImpl.findByInvitationToken(token);

        // then
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get().getId()).isEqualTo("user1");
        assertThat(findUser.get().getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("초대 토큰이 없는 경우 빈 Optional 반환")
    void findByInvitationTokenEmpty() {
        // given
        String invalidToken = "invalid-token";

        // when
        Optional<User> findUser = userRepositoryImpl.findByInvitationToken(invalidToken);

        // then
        assertThat(findUser.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("삭제된 유저는 초대 토큰으로 조회되지 않는다.")
    void findByInvitationTokenDeletedUser() {
        // given
        User user = User.createInvitedUser("user1", "홍길동", "test@example.com",
                OriginCompanyType.SKAX, "9 ~ 6", LocalDate.of(2025, 1, 1));
        userRepositoryImpl.save(user);
        String token = user.getInvitationToken();
        user.deleteUser();
        em.flush();
        em.clear();

        // when
        Optional<User> findUser = userRepositoryImpl.findByInvitationToken(token);

        // then
        assertThat(findUser.isEmpty()).isTrue();
    }
}