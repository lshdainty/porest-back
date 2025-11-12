package com.lshdainty.porest.work.repository;

import com.lshdainty.porest.work.domain.WorkHistory;

import java.util.List;
import java.util.Optional;

public interface WorkHistoryRepository {
    // 신규 업무 이력 저장
    void save(WorkHistory workHistory);
    // 단건 업무 이력 조회
    Optional<WorkHistory> findById(Long id);
    // 전체 업무 이력 조회
    List<WorkHistory> findAll();
    // 업무 이력 삭제 (Soft Delete)
    void delete(WorkHistory workHistory);
}
