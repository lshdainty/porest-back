package com.lshdainty.porest.department.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.department.controller.dto.DepartmentApiDto;
import com.lshdainty.porest.department.service.DepartmentService;
import com.lshdainty.porest.department.service.dto.DepartmentServiceDto;
import com.lshdainty.porest.department.service.dto.UserDepartmentServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DepartmentApiController {
    private final DepartmentService departmentService;

    @PostMapping("/api/v1/departments")
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

    @PutMapping("/api/v1/departments/{id}")
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

    @DeleteMapping("/api/v1/departments/{id}")
    public ApiResponse deleteDepartment(@PathVariable("id") Long departmentId) {
        departmentService.delete(departmentId);
        return ApiResponse.success();
    }

    @GetMapping("/api/v1/departments/{id}")
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

    @GetMapping("/api/v1/departments/{id}/children")
    public ApiResponse searchDepartmentByIdWithChildren(@PathVariable("id") Long departmentId) {
        DepartmentServiceDto serviceDto = departmentService.searchDepartmentByIdWithChildren(departmentId);
        DepartmentApiDto.SearchDepartmentWithChildrenResp responseDto =
                DepartmentApiDto.SearchDepartmentWithChildrenResp.fromServiceDto(serviceDto);
        return ApiResponse.success(responseDto);
    }

    @PostMapping("/api/v1/departments/{departmentId}/users")
    public ApiResponse registDepartmentUser(
            @PathVariable("departmentId") Long departmentId,
            @RequestBody DepartmentApiDto.RegistDepartmentUserReq data) {
        Long userDepartmentId = departmentService.registUserDepartment(UserDepartmentServiceDto.builder()
                .userId(data.getUserId())
                .departmentId(departmentId)
                .mainYN(data.getMainYn())
                .build()
        );
        return ApiResponse.success(new DepartmentApiDto.RegistDepartmentUserResp(userDepartmentId));
    }

    @DeleteMapping("/api/v1/departments/{departmentId}/users/{userId}")
    public ApiResponse deleteDepartmentUser(
            @PathVariable("departmentId") Long departmentId,
            @PathVariable("userId") String userId) {
        departmentService.deleteUserDepartment(userId, departmentId);
        return ApiResponse.success();
    }
}
