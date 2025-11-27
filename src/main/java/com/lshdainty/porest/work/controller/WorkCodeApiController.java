package com.lshdainty.porest.work.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.work.controller.dto.WorkCodeApiDto;
import com.lshdainty.porest.work.service.WorkCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WorkCodeApiController {
    private final WorkCodeService workCodeService;

    /**
     * 업무 코드 생성
     * POST /api/v1/work-codes
     */
    @PostMapping("/api/v1/work-codes")
    @PreAuthorize("hasAuthority('WORK_MANAGE')")
    public ApiResponse createWorkCode(@RequestBody WorkCodeApiDto.CreateWorkCodeReq data) {
        Long workCodeSeq = workCodeService.createWorkCode(
                data.getWorkCode(),
                data.getWorkCodeName(),
                data.getCodeType(),
                data.getParentWorkCodeSeq(),
                data.getOrderSeq()
        );
        return ApiResponse.success(new WorkCodeApiDto.CreateWorkCodeResp(workCodeSeq));
    }

    /**
     * 업무 코드 수정
     * PUT /api/v1/work-codes/{seq}
     */
    @PutMapping("/api/v1/work-codes/{seq}")
    @PreAuthorize("hasAuthority('WORK_MANAGE')")
    public ApiResponse updateWorkCode(@PathVariable("seq") Long seq,
                                       @RequestBody WorkCodeApiDto.UpdateWorkCodeReq data) {
        workCodeService.updateWorkCode(
                seq,
                data.getWorkCode(),
                data.getWorkCodeName(),
                data.getParentWorkCodeSeq(),
                data.getOrderSeq()
        );
        return ApiResponse.success();
    }

    /**
     * 업무 코드 삭제 (Soft Delete)
     * DELETE /api/v1/work-codes/{seq}
     */
    @DeleteMapping("/api/v1/work-codes/{seq}")
    @PreAuthorize("hasAuthority('WORK_MANAGE')")
    public ApiResponse deleteWorkCode(@PathVariable("seq") Long seq) {
        workCodeService.deleteWorkCode(seq);
        return ApiResponse.success();
    }
}
