package com.lshdainty.porest.work.repository;

import com.lshdainty.porest.work.domain.WorkCode;

import java.util.Optional;

public interface WorkCodeRepository {
    // 코드로 업무 코드 조회
    Optional<WorkCode> findByCode(String code);
}
