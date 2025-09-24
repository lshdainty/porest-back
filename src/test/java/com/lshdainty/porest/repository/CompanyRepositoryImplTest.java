package com.lshdainty.porest.repository;

import com.lshdainty.porest.company.repository.CompanyCustomRepositoryImpl;
import com.lshdainty.porest.user.repository.UserRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DataJpaTest
@Import(UserRepositoryImpl.class)
@Transactional
@DisplayName("JPA 유저 레포지토리 테스트")
class CompanyRepositoryImplTest {
    @Autowired
    private CompanyCustomRepositoryImpl companyRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("test")
    void find() {

    }
}
