package com.lshdainty.porest.repository;

import com.lshdainty.porest.company.domain.Company;
import com.lshdainty.porest.company.repository.CompanyCustomRepositoryImpl;
import com.lshdainty.porest.department.domain.Department;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@Import({CompanyCustomRepositoryImpl.class, TestQuerydslConfig.class})
@Transactional
@DisplayName("JPA 회사 레포지토리 테스트")
class CompanyRepositoryImplTest {
    @Autowired
    private CompanyCustomRepositoryImpl companyRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("회사 저장 및 단건 조회")
    void save() {
        // given
        String id = "company1";
        String name = "테스트 회사";
        String desc = "회사 설명";

        Company company = Company.createCompany(id, name, desc);

        // when
        companyRepository.save(company);
        em.flush();
        em.clear();

        // then
        Optional<Company> findCompany = companyRepository.findById(id);
        assertThat(findCompany.isPresent()).isTrue();
        assertThat(findCompany.get().getId()).isEqualTo(id);
        assertThat(findCompany.get().getName()).isEqualTo(name);
        assertThat(findCompany.get().getDesc()).isEqualTo(desc);
    }

    @Test
    @DisplayName("단건 조회 시 회사가 없어도 Null이 반환되면 안된다.")
    void findByIdEmpty() {
        // given
        String companyId = "invalid-company";

        // when
        Optional<Company> findCompany = companyRepository.findById(companyId);

        // then
        assertThat(findCompany.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("최상위 회사 조회")
    void find() {
        // given
        Company company = Company.createCompany("company1", "테스트 회사", "설명");
        companyRepository.save(company);
        em.flush();
        em.clear();

        // when
        Optional<Company> findCompany = companyRepository.find();

        // then
        assertThat(findCompany.isPresent()).isTrue();
        assertThat(findCompany.get().getId()).isEqualTo("company1");
    }

    @Test
    @DisplayName("최상위 회사가 없어도 Null이 반환되면 안된다.")
    void findEmpty() {
        // given & when
        Optional<Company> findCompany = companyRepository.find();

        // then
        assertThat(findCompany.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("회사와 최상위 부서를 함께 조회")
    void findByIdWithDepartments() {
        // given
        Company company = Company.createCompany("company1", "테스트 회사", "설명");
        companyRepository.save(company);

        Department dept1 = Department.createDepartment("개발팀", "개발팀", null, null, 1L, "개발 부서", "#FF0000", company);
        Department dept2 = Department.createDepartment("인사팀", "인사팀", null, null, 2L, "인사 부서", "#00FF00", company);
        em.persist(dept1);
        em.persist(dept2);

        em.flush();
        em.clear();

        // when
        Optional<Company> findCompany = companyRepository.findByIdWithDepartments("company1");

        // then
        assertThat(findCompany.isPresent()).isTrue();
        assertThat(findCompany.get().getDepartments()).hasSize(2);
        assertThat(findCompany.get().getDepartments())
                .extracting("name")
                .containsExactlyInAnyOrder("개발팀", "인사팀");
    }

    @Test
    @DisplayName("회사 수정")
    void updateCompany() {
        // given
        Company company = Company.createCompany("company1", "테스트 회사", "설명");
        companyRepository.save(company);
        em.flush();
        em.clear();

        // when
        Company foundCompany = companyRepository.findById("company1").orElseThrow();
        foundCompany.updateCompany("수정된 회사", "수정된 설명");
        em.flush();
        em.clear();

        // then
        Company updatedCompany = companyRepository.findById("company1").orElseThrow();
        assertThat(updatedCompany.getName()).isEqualTo("수정된 회사");
        assertThat(updatedCompany.getDesc()).isEqualTo("수정된 설명");
    }
}
