package com.lshdainty.porest.department.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.department.controller.dto.DepartmentApiDto;
import com.lshdainty.porest.department.service.DepartmentService;
import com.lshdainty.porest.department.service.dto.DepartmentServiceDto;
import com.lshdainty.porest.department.service.dto.UserDepartmentServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DepartmentApiController {
    private final DepartmentService departmentService;

    @PostMapping("/api/v1/departments")
    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
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
    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
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
    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    public ApiResponse deleteDepartment(@PathVariable("id") Long departmentId) {
        departmentService.delete(departmentId);
        return ApiResponse.success();
    }

    @GetMapping("/api/v1/departments/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
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
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    public ApiResponse searchDepartmentByIdWithChildren(@PathVariable("id") Long departmentId) {
        DepartmentServiceDto serviceDto = departmentService.searchDepartmentByIdWithChildren(departmentId);
        DepartmentApiDto.SearchDepartmentWithChildrenResp responseDto =
                DepartmentApiDto.SearchDepartmentWithChildrenResp.fromServiceDto(serviceDto);
        return ApiResponse.success(responseDto);
    }

    @PostMapping("/api/v1/departments/{departmentId}/users")
    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    public ApiResponse registDepartmentUsers(
            @PathVariable("departmentId") Long departmentId,
            @RequestBody DepartmentApiDto.RegistDepartmentUserReq data) {
        // DTO 변환
        List<UserDepartmentServiceDto> userDepartmentServiceDtos = data.getUsers().stream()
                .map(userInfo -> UserDepartmentServiceDto.builder()
                        .userId(userInfo.getUserId())
                        .mainYN(userInfo.getMainYn())
                        .build())
                .toList();

        List<Long> userDepartmentIds = departmentService.registUserDepartments(userDepartmentServiceDtos, departmentId);
        return ApiResponse.success(new DepartmentApiDto.RegistDepartmentUserResp(userDepartmentIds));
    }

    @DeleteMapping("/api/v1/departments/{departmentId}/users")
    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    public ApiResponse deleteDepartmentUsers(
            @PathVariable("departmentId") Long departmentId,
            @RequestBody DepartmentApiDto.DeleteDepartmentUserReq data) {
        departmentService.deleteUserDepartments(data.getUserIds(), departmentId);
        return ApiResponse.success();
    }

    @GetMapping("/api/v1/departments/{departmentId}/users")
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    public ApiResponse getDepartmentUsers(@PathVariable("departmentId") Long departmentId) {
        DepartmentServiceDto serviceDto = departmentService.getUsersInAndNotInDepartment(departmentId);

        DepartmentApiDto.GetDepartmentUsersResp responseDto = new DepartmentApiDto.GetDepartmentUsersResp(
                serviceDto.getId(),
                serviceDto.getName(),
                serviceDto.getNameKR(),
                serviceDto.getParentId(),
                serviceDto.getHeadUserId(),
                serviceDto.getLevel(),
                serviceDto.getDesc(),
                serviceDto.getColor(),
                serviceDto.getCompanyId(),
                serviceDto.getUsersInDepartment().stream()
                        .map(userDepartment -> new DepartmentApiDto.UserInfo(
                                userDepartment.getUser().getId(),
                                userDepartment.getUser().getName(),
                                userDepartment.getMainYN()
                        ))
                        .toList(),
                serviceDto.getUsersNotInDepartment().stream()
                        .map(userDepartment -> new DepartmentApiDto.UserInfo(
                                userDepartment.getUser().getId(),
                                userDepartment.getUser().getName(),
                                userDepartment.getMainYN()
                        ))
                        .toList()
        );

        return ApiResponse.success(responseDto);
    }
}
