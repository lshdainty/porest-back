package com.lshdainty.porest.work.service;

import com.lshdainty.porest.common.message.MessageKey;
import com.lshdainty.porest.common.util.MessageResolver;
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
public class WorkCodeService {
    private final WorkCodeRepository workCodeRepository;
    private final MessageResolver messageResolver;

    /**
     * 동적 조건으로 업무 코드 목록 조회
     *
     * @param parentWorkCode 부모 코드 문자열 (null 가능)
     * @param parentWorkCodeSeq 부모 코드 seq (null 가능)
     * @param parentIsNull 최상위 코드 조회 여부 (null 가능)
     * @param type 코드 타입 (null 가능)
     * @return 조건에 맞는 업무 코드 목록
     *
     * 사용 예시:
     * - findWorkCodes(null, null, true, CodeType.LABEL) → 최상위 LABEL 조회
     * - findWorkCodes("work_group", null, null, CodeType.OPTION) → work_group의 OPTION 하위 코드 조회
     * - findWorkCodes(null, 1L, null, CodeType.OPTION) → seq가 1인 부모의 OPTION 하위 코드 조회
     * - findWorkCodes("assignment", null, null, CodeType.LABEL) → assignment의 LABEL 하위 코드 조회
     * - findWorkCodes("work_part", null, null, CodeType.OPTION) → work_part의 OPTION 하위 코드 조회
     */
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

    /**
     * 업무 코드 생성
     *
     * @param code 코드 값
     * @param name 코드명
     * @param type 코드 타입 (LABEL/OPTION)
     * @param parentSeq 부모 코드 seq (null 가능)
     * @param orderSeq 정렬 순서
     * @return 생성된 업무 코드 seq
     */
    @Transactional
    public Long createWorkCode(String code, String name, CodeType type, Long parentSeq, Integer orderSeq) {
        // 부모 코드 조회 (parentSeq가 있는 경우)
        WorkCode parent = null;
        if (parentSeq != null) {
            parent = workCodeRepository.findBySeq(parentSeq)
                    .orElseThrow(() -> new IllegalArgumentException(messageResolver.getMessage(MessageKey.NOT_FOUND_WORK_CODE)));
        }

        // 코드 중복 체크
        workCodeRepository.findByCode(code).ifPresent(wc -> {
            throw new IllegalArgumentException(messageResolver.getMessage(MessageKey.VALIDATE_DUPLICATE_WORK_CODE));
        });

        // 업무 코드 생성
        WorkCode workCode = WorkCode.createWorkCode(code, name, type, parent, orderSeq);
        workCodeRepository.save(workCode);

        log.info("업무 코드 생성 완료: code={}, name={}, type={}, parentSeq={}, orderSeq={}",
                code, name, type, parentSeq, orderSeq);

        return workCode.getSeq();
    }

    /**
     * 업무 코드 수정
     *
     * @param seq 수정할 업무 코드 seq
     * @param code 코드 값
     * @param name 코드명
     * @param parentSeq 부모 코드 seq (null 가능)
     * @param orderSeq 정렬 순서
     */
    @Transactional
    public void updateWorkCode(Long seq, String code, String name, Long parentSeq, Integer orderSeq) {
        // 업무 코드 조회
        WorkCode workCode = workCodeRepository.findBySeq(seq)
                .orElseThrow(() -> new IllegalArgumentException(messageResolver.getMessage(MessageKey.NOT_FOUND_WORK_CODE)));

        // 부모 코드 조회 (parentSeq가 있는 경우)
        WorkCode parent = null;
        if (parentSeq != null) {
            parent = workCodeRepository.findBySeq(parentSeq)
                    .orElseThrow(() -> new IllegalArgumentException(messageResolver.getMessage(MessageKey.NOT_FOUND_WORK_CODE_PARENT)));

            // 자기 자신을 부모로 설정하는 것 방지
            if (seq.equals(parentSeq)) {
                throw new IllegalArgumentException(messageResolver.getMessage(MessageKey.VALIDATE_INVALID_WORK_CODE_PARENT_SELF));
            }
        }

        // 코드 중복 체크 (자신 제외)
        if (code != null) {
            workCodeRepository.findByCode(code).ifPresent(wc -> {
                if (!wc.getSeq().equals(seq)) {
                    throw new IllegalArgumentException(messageResolver.getMessage(MessageKey.VALIDATE_DUPLICATE_WORK_CODE));
                }
            });
        }

        // 업무 코드 수정
        workCode.updateWorkCode(code, name, parent, orderSeq);

        log.info("업무 코드 수정 완료: seq={}, code={}, name={}, parentSeq={}, orderSeq={}",
                seq, code, name, parentSeq, orderSeq);
    }

    /**
     * 업무 코드 삭제 (Soft Delete)
     *
     * @param seq 삭제할 업무 코드 seq
     */
    @Transactional
    public void deleteWorkCode(Long seq) {
        // 업무 코드 조회
        WorkCode workCode = workCodeRepository.findBySeq(seq)
                .orElseThrow(() -> new IllegalArgumentException(messageResolver.getMessage(MessageKey.NOT_FOUND_WORK_CODE)));

        // 업무 코드 삭제 (Soft Delete)
        workCode.deleteWorkCode();

        log.info("업무 코드 삭제 완료: seq={}, code={}", seq, workCode.getCode());
    }
}
