package com.lshdainty.porest.work.service;

import com.lshdainty.porest.common.exception.DuplicateException;
import com.lshdainty.porest.common.exception.EntityNotFoundException;
import com.lshdainty.porest.common.exception.ErrorCode;
import com.lshdainty.porest.common.exception.InvalidValueException;
import com.lshdainty.porest.work.domain.WorkCode;
import com.lshdainty.porest.work.repository.WorkCodeRepository;
import com.lshdainty.porest.work.service.dto.WorkCodeServiceDto;
import com.lshdainty.porest.work.type.CodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WorkCodeServiceImpl implements WorkCodeService {
    private final WorkCodeRepository workCodeRepository;

    @Override
    public List<WorkCodeServiceDto> findWorkCodes(String parentWorkCode, Long parentWorkCodeSeq, Boolean parentIsNull, CodeType type) {
        List<WorkCode> workCodes = workCodeRepository.findAllByConditions(parentWorkCode, parentWorkCodeSeq, parentIsNull, type);
        return workCodes.stream()
                .map(wc -> WorkCodeServiceDto.builder()
                        .seq(wc.getSeq())
                        .code(wc.getCode())
                        .name(wc.getName())
                        .type(wc.getType())
                        .orderSeq(wc.getOrderSeq())
                        .parentSeq(wc.getParent() != null ? wc.getParent().getSeq() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createWorkCode(String code, String name, CodeType type, Long parentSeq, Integer orderSeq) {
        // 부모 코드 조회 (parentSeq가 있는 경우)
        WorkCode parent = null;
        if (parentSeq != null) {
            parent = workCodeRepository.findBySeq(parentSeq)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WORK_CODE_NOT_FOUND));
        }

        // 코드 중복 체크
        workCodeRepository.findByCode(code).ifPresent(wc -> {
            throw new DuplicateException(ErrorCode.WORK_CODE_DUPLICATE);
        });

        // 업무 코드 생성
        WorkCode workCode = WorkCode.createWorkCode(code, name, type, parent, orderSeq);
        workCodeRepository.save(workCode);

        log.info("업무 코드 생성 완료: code={}, name={}, type={}, parentSeq={}, orderSeq={}",
                code, name, type, parentSeq, orderSeq);

        return workCode.getSeq();
    }

    @Override
    @Transactional
    public void updateWorkCode(Long seq, String code, String name, Long parentSeq, Integer orderSeq) {
        // 업무 코드 조회
        WorkCode workCode = workCodeRepository.findBySeq(seq)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WORK_CODE_NOT_FOUND));

        // 부모 코드 조회 (parentSeq가 있는 경우)
        WorkCode parent = null;
        if (parentSeq != null) {
            parent = workCodeRepository.findBySeq(parentSeq)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WORK_CODE_NOT_FOUND));

            // 자기 자신을 부모로 설정하는 것 방지
            if (seq.equals(parentSeq)) {
                throw new InvalidValueException(ErrorCode.WORK_CODE_INVALID_PARENT);
            }
        }

        // 코드 중복 체크 (자신 제외)
        if (code != null) {
            workCodeRepository.findByCode(code).ifPresent(wc -> {
                if (!wc.getSeq().equals(seq)) {
                    throw new DuplicateException(ErrorCode.WORK_CODE_DUPLICATE);
                }
            });
        }

        // 업무 코드 수정
        workCode.updateWorkCode(code, name, parent, orderSeq);

        log.info("업무 코드 수정 완료: seq={}, code={}, name={}, parentSeq={}, orderSeq={}",
                seq, code, name, parentSeq, orderSeq);
    }

    @Override
    @Transactional
    public void deleteWorkCode(Long seq) {
        // 업무 코드 조회
        WorkCode workCode = workCodeRepository.findBySeq(seq)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WORK_CODE_NOT_FOUND));

        // 업무 코드 삭제 (Soft Delete)
        workCode.deleteWorkCode();

        log.info("업무 코드 삭제 완료: seq={}, code={}", seq, workCode.getCode());
    }
}
