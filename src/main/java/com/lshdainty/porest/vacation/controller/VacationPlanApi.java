package com.lshdainty.porest.vacation.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.vacation.controller.dto.VacationPlanApiDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vacation Plan", description = "휴가 플랜 관리 API")
public interface VacationPlanApi {

    // ========================================
    // Plan CRUD
    // ========================================

    @Operation(
            summary = "휴가 플랜 생성",
            description = "새로운 휴가 플랜을 생성합니다. 정책 ID 목록을 함께 전달하면 정책이 포함된 플랜이 생성됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 생성 성공",
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.PlanResp.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "플랜 코드 중복"
            )
    })
    @PostMapping("/api/v1/vacation-plans")
    ApiResponse createPlan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "플랜 생성 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.CreatePlanReq.class))
            )
            @RequestBody VacationPlanApiDto.CreatePlanReq data
    );

    @Operation(
            summary = "전체 휴가 플랜 목록 조회",
            description = "모든 휴가 플랜 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 목록 조회 성공"
            )
    })
    @GetMapping("/api/v1/vacation-plans")
    ApiResponse getAllPlans();

    @Operation(
            summary = "휴가 플랜 상세 조회",
            description = "특정 휴가 플랜의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 조회 성공",
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.PlanResp.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "플랜 없음"
            )
    })
    @GetMapping("/api/v1/vacation-plans/{code}")
    ApiResponse getPlan(
            @Parameter(description = "플랜 코드", example = "FULL_TIME", required = true)
            @PathVariable("code") String code
    );

    @Operation(
            summary = "휴가 플랜 수정",
            description = "휴가 플랜의 이름과 설명을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 수정 성공",
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.PlanResp.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "플랜 없음"
            )
    })
    @PutMapping("/api/v1/vacation-plans/{code}")
    ApiResponse updatePlan(
            @Parameter(description = "플랜 코드", example = "FULL_TIME", required = true)
            @PathVariable("code") String code,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "플랜 수정 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.UpdatePlanReq.class))
            )
            @RequestBody VacationPlanApiDto.UpdatePlanReq data
    );

    @Operation(
            summary = "휴가 플랜 삭제",
            description = "휴가 플랜을 삭제합니다 (소프트 삭제)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "플랜 없음"
            )
    })
    @DeleteMapping("/api/v1/vacation-plans/{code}")
    ApiResponse deletePlan(
            @Parameter(description = "플랜 코드", example = "FULL_TIME", required = true)
            @PathVariable("code") String code
    );

    // ========================================
    // Plan-Policy 관리
    // ========================================

    @Operation(
            summary = "플랜에 정책 추가",
            description = "휴가 플랜에 새로운 정책을 추가합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정책 추가 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "플랜 또는 정책 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 포함된 정책"
            )
    })
    @PostMapping("/api/v1/vacation-plans/{code}/policies/{policyId}")
    ApiResponse addPolicyToPlan(
            @Parameter(description = "플랜 코드", example = "FULL_TIME", required = true)
            @PathVariable("code") String code,
            @Parameter(description = "정책 ID", example = "1", required = true)
            @PathVariable("policyId") Long policyId
    );

    @Operation(
            summary = "플랜에서 정책 제거",
            description = "휴가 플랜에서 정책을 제거합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정책 제거 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "플랜 또는 정책 없음"
            )
    })
    @DeleteMapping("/api/v1/vacation-plans/{code}/policies/{policyId}")
    ApiResponse removePolicyFromPlan(
            @Parameter(description = "플랜 코드", example = "FULL_TIME", required = true)
            @PathVariable("code") String code,
            @Parameter(description = "정책 ID", example = "1", required = true)
            @PathVariable("policyId") Long policyId
    );

    @Operation(
            summary = "플랜 정책 전체 업데이트",
            description = "휴가 플랜의 정책 목록을 전체 교체합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정책 업데이트 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "플랜 또는 정책 없음"
            )
    })
    @PutMapping("/api/v1/vacation-plans/{code}/policies")
    ApiResponse updatePlanPolicies(
            @Parameter(description = "플랜 코드", example = "FULL_TIME", required = true)
            @PathVariable("code") String code,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "정책 ID 목록",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.UpdatePlanPoliciesReq.class))
            )
            @RequestBody VacationPlanApiDto.UpdatePlanPoliciesReq data
    );

    // ========================================
    // User-Plan 관리
    // ========================================

    @Operation(
            summary = "사용자에게 플랜 할당",
            description = "사용자에게 휴가 플랜을 할당합니다. 플랜의 REPEAT_GRANT 정책에 대한 스케줄이 자동 생성됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 할당 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 플랜 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 할당된 플랜"
            )
    })
    @PostMapping("/api/v1/users/{userId}/vacation-plans")
    ApiResponse assignPlanToUser(
            @Parameter(description = "사용자 ID", example = "user123", required = true)
            @PathVariable("userId") String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "플랜 할당 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.AssignPlanReq.class))
            )
            @RequestBody VacationPlanApiDto.AssignPlanReq data
    );

    @Operation(
            summary = "사용자에게 여러 플랜 할당",
            description = "사용자에게 여러 휴가 플랜을 일괄 할당합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 할당 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 플랜 없음"
            )
    })
    @PostMapping("/api/v1/users/{userId}/vacation-plans/batch")
    ApiResponse assignPlansToUser(
            @Parameter(description = "사용자 ID", example = "user123", required = true)
            @PathVariable("userId") String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "플랜 목록 할당 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = VacationPlanApiDto.AssignPlansReq.class))
            )
            @RequestBody VacationPlanApiDto.AssignPlansReq data
    );

    @Operation(
            summary = "사용자에게서 플랜 회수",
            description = "사용자에게서 휴가 플랜을 회수합니다. 다른 플랜에 없는 정책의 스케줄도 함께 삭제됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 회수 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 플랜 없음"
            )
    })
    @DeleteMapping("/api/v1/users/{userId}/vacation-plans/{code}")
    ApiResponse revokePlanFromUser(
            @Parameter(description = "사용자 ID", example = "user123", required = true)
            @PathVariable("userId") String userId,
            @Parameter(description = "플랜 코드", example = "FULL_TIME", required = true)
            @PathVariable("code") String code
    );

    @Operation(
            summary = "사용자의 플랜 목록 조회",
            description = "사용자에게 할당된 휴가 플랜 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "플랜 목록 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자 없음"
            )
    })
    @GetMapping("/api/v1/users/{userId}/vacation-plans")
    ApiResponse getUserPlans(
            @Parameter(description = "사용자 ID", example = "user123", required = true)
            @PathVariable("userId") String userId
    );
}
