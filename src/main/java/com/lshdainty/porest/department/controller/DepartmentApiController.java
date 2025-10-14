package com.lshdainty.porest.department.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.department.controller.dto.DepartmentApiDto;
import com.lshdainty.porest.department.service.DepartmentService;
import com.lshdainty.porest.department.service.dto.DepartmentServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DepartmentApiController {
    private final DepartmentService departmentService;

    @PostMapping("/api/v1/department")
    public ApiResponse registDepartment(@RequestBody DepartmentApiDto.RegistDepartmentReq data) {
        Long departmentId = departmentService.regist(DepartmentServiceDto.builder()
                .name(data.getDepartmentName())
                .nameKR(data.getDepartmentNameKr())
                .parentId(data.getParentId())
                .headUserId(data.getHeadUserId())
                .level(data.getTreeLevel())
                .desc(data.getDepartmentDesc())
                .color(data.getColorCode())
                .companyId(data.getCompanyId())
                .build()
        );
        return ApiResponse.success(new DepartmentApiDto.RegistDepartmentResp(departmentId));
    }

    @PutMapping("/api/v1/department/{id}")
    public ApiResponse editDepartment(@PathVariable("id") Long departmentId, @RequestBody DepartmentApiDto.EditDepartmentReq data) {
        departmentService.edit(DepartmentServiceDto.builder()
                .id(departmentId)
                .name(data.getDepartmentName())
                .nameKR(data.getDepartmentNameKr())
                .parentId(data.getParentId())
                .headUserId(data.getHeadUserId())
                .level(data.getTreeLevel())
                .desc(data.getDepartmentDesc())
                .color(data.getColorCode())
                .build()
        );
        return ApiResponse.success();
    }

    @DeleteMapping("/api/v1/department/{id}")
    public ApiResponse deleteCompany(@PathVariable("id") Long departmentId) {
        departmentService.delete(departmentId);
        return ApiResponse.success();
    }

    @GetMapping("/api/v1/department/{id}")
    public ApiResponse searchDepartmentById(@PathVariable("id") Long departmentId) {
        DepartmentServiceDto serviceDto = departmentService.searchDepartmentById(departmentId);

        return ApiResponse.success(new DepartmentApiDto.SearchDepartmentResp(
                serviceDto.getId(),
                serviceDto.getName(),
                serviceDto.getNameKR(),
                serviceDto.getParentId(),
                serviceDto.getHeadUserId(),
                serviceDto.getLevel(),
                serviceDto.getDesc(),
                serviceDto.getColor(),
                serviceDto.getCompanyId()
        ));
    }

    @GetMapping("/api/v1/department/{id}/children")
    public ApiResponse searchDepartmentByIdWithChildren(@PathVariable("id") Long departmentId) {
        DepartmentServiceDto serviceDto = departmentService.searchDepartmentByIdWithChildren(departmentId);
        DepartmentApiDto.SearchDepartmentWithChildrenResp responseDto = convertToResponseDtoWithChildren(serviceDto);
        return ApiResponse.success(responseDto);
    }

    /**
     * Service DTO -> Controller DTO 변환 (자식 포함, 재귀적)
     */
    private DepartmentApiDto.SearchDepartmentWithChildrenResp convertToResponseDtoWithChildren(DepartmentServiceDto serviceDto) {
        if (serviceDto == null) return null;

        return new DepartmentApiDto.SearchDepartmentWithChildrenResp(
                serviceDto.getId(),
                serviceDto.getName(),
                serviceDto.getNameKR(),
                serviceDto.getParentId(),
                serviceDto.getHeadUserId(),
                serviceDto.getLevel(),
                serviceDto.getDesc(),
                serviceDto.getColor(),
                serviceDto.getCompanyId(),
                serviceDto.getChildren() != null
                        ? serviceDto.getChildren().stream()
                        .map(this::convertToResponseDtoWithChildren)
                        .toList()
                        : null
        );
    }
}
