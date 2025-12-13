package com.lshdainty.porest.vacation.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.vacation.controller.dto.VacationPlanApiDto;
import com.lshdainty.porest.vacation.service.VacationPlanService;
import com.lshdainty.porest.vacation.service.dto.VacationPlanServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VacationPlanApiController implements VacationPlanApi {
    private final VacationPlanService vacationPlanService;

    // ========================================
    // Plan CRUD
    // ========================================

    @Override
    public ApiResponse createPlan(VacationPlanApiDto.CreatePlanReq data) {
        log.info("휴가 플랜 생성 요청: code={}, name={}", data.getCode(), data.getName());

        VacationPlanServiceDto result;
        if (data.getPolicyIds() != null && !data.getPolicyIds().isEmpty()) {
            result = vacationPlanService.createPlanWithPolicies(
                    data.getCode(),
                    data.getName(),
                    data.getDesc(),
                    data.getPolicyIds()
            );
        } else {
            result = vacationPlanService.createPlan(
                    data.getCode(),
                    data.getName(),
                    data.getDesc()
            );
        }

        return ApiResponse.success(VacationPlanApiDto.PlanResp.from(result));
    }

    @Override
    public ApiResponse getAllPlans() {
        log.info("전체 휴가 플랜 목록 조회 요청");

        List<VacationPlanServiceDto> plans = vacationPlanService.getAllPlans();
        List<VacationPlanApiDto.PlanResp> response = plans.stream()
                .map(VacationPlanApiDto.PlanResp::from)
                .toList();

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse getPlan(String code) {
        log.info("휴가 플랜 상세 조회 요청: code={}", code);

        VacationPlanServiceDto plan = vacationPlanService.getPlan(code);
        return ApiResponse.success(VacationPlanApiDto.PlanResp.from(plan));
    }

    @Override
    public ApiResponse updatePlan(String code, VacationPlanApiDto.UpdatePlanReq data) {
        log.info("휴가 플랜 수정 요청: code={}", code);

        VacationPlanServiceDto result = vacationPlanService.updatePlan(
                code,
                data.getName(),
                data.getDesc()
        );

        return ApiResponse.success(VacationPlanApiDto.PlanResp.from(result));
    }

    @Override
    public ApiResponse deletePlan(String code) {
        log.info("휴가 플랜 삭제 요청: code={}", code);

        vacationPlanService.deletePlan(code);
        return ApiResponse.success();
    }

    // ========================================
    // Plan-Policy 관리
    // ========================================

    @Override
    public ApiResponse addPolicyToPlan(String code, Long policyId) {
        log.info("플랜에 정책 추가 요청: code={}, policyId={}", code, policyId);

        vacationPlanService.addPolicyToPlan(code, policyId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse removePolicyFromPlan(String code, Long policyId) {
        log.info("플랜에서 정책 제거 요청: code={}, policyId={}", code, policyId);

        vacationPlanService.removePolicyFromPlan(code, policyId);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse updatePlanPolicies(String code, VacationPlanApiDto.UpdatePlanPoliciesReq data) {
        log.info("플랜 정책 전체 업데이트 요청: code={}", code);

        vacationPlanService.updatePlanPolicies(code, data.getPolicyIds());
        return ApiResponse.success();
    }

    // ========================================
    // User-Plan 관리
    // ========================================

    @Override
    public ApiResponse assignPlanToUser(String userId, VacationPlanApiDto.AssignPlanReq data) {
        log.info("사용자에게 플랜 할당 요청: userId={}, planCode={}", userId, data.getPlanCode());

        vacationPlanService.assignPlanToUser(userId, data.getPlanCode());
        return ApiResponse.success();
    }

    @Override
    public ApiResponse assignPlansToUser(String userId, VacationPlanApiDto.AssignPlansReq data) {
        log.info("사용자에게 여러 플랜 할당 요청: userId={}, planCodes={}", userId, data.getPlanCodes());

        vacationPlanService.assignPlansToUser(userId, data.getPlanCodes());
        return ApiResponse.success();
    }

    @Override
    public ApiResponse revokePlanFromUser(String userId, String code) {
        log.info("사용자에게서 플랜 회수 요청: userId={}, planCode={}", userId, code);

        vacationPlanService.revokePlanFromUser(userId, code);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getUserPlans(String userId) {
        log.info("사용자의 플랜 목록 조회 요청: userId={}", userId);

        List<VacationPlanServiceDto> plans = vacationPlanService.getUserPlans(userId);
        List<VacationPlanApiDto.PlanResp> response = plans.stream()
                .map(VacationPlanApiDto.PlanResp::from)
                .toList();

        return ApiResponse.success(response);
    }
}
