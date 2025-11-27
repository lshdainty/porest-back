package com.lshdainty.porest.work.repository;

import com.lshdainty.porest.work.domain.WorkCode;
import com.lshdainty.porest.work.type.CodeType;

import java.util.List;
import java.util.Optional;

public interface WorkCodeRepository {
    // 업무 코드 저장
    void save(WorkCode workCode);

    // 코드로 업무 코드 조회
    Optional<WorkCode> findByCode(String code);

    // Seq로 업무 코드 조회
    Optional<WorkCode> findBySeq(Long seq);

    // 동적 조건으로 업무 코드 목록 조회
    List<WorkCode> findAllByConditions(String parentWorkCode, Long parentWorkCodeSeq, Boolean parentIsNull, CodeType type);
}
