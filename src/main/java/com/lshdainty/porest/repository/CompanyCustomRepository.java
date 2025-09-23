package com.lshdainty.porest.repository;

import com.lshdainty.porest.domain.Company;

import java.util.List;

public interface CompanyCustomRepository {
    // 신규 회사 저장
    void save(Company company);
    // 단건 회사 조회
    Company findById(String id);
    // 단건 회사 조회(부서 포함)
    Company findByIdWithDepartments(String id);
}
