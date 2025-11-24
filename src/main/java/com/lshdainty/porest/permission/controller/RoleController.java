package com.lshdainty.porest.permission.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.permission.domain.Permission;
import com.lshdainty.porest.permission.domain.Role;
import com.lshdainty.porest.permission.service.RoleService;
import com.lshdainty.porest.permission.controller.dto.RoleApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<List<RoleApiDto.RoleResp>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleApiDto.RoleResp> resp = roles.stream()
                .map(role -> new RoleApiDto.RoleResp(
                        role.getName(),
                        role.getDescription(),
                        role.getPermissions().stream().map(Permission::getName).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        return ApiResponse.success(resp);
    }

    @GetMapping("/roles/{roleName}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<RoleApiDto.RoleResp> getRole(@PathVariable String roleName) {
        Role role = roleService.getRole(roleName);
        return ApiResponse.success(new RoleApiDto.RoleResp(
                role.getName(),
                role.getDescription(),
                role.getPermissions().stream().map(Permission::getName).collect(Collectors.toList())
        ));
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ApiResponse<String> createRole(@RequestBody RoleApiDto.CreateRoleReq req) {
        Role role = roleService.createRole(req.getRoleName(), req.getDescription());
        return ApiResponse.success(role.getName());
    }

    @PutMapping("/roles/{roleName}/permissions")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ApiResponse<Void> updateRolePermissions(@PathVariable String roleName, @RequestBody RoleApiDto.UpdateRolePermissionsReq req) {
        roleService.updateRolePermissions(roleName, req.getPermissionNames());
        return ApiResponse.success();
    }
    
    @DeleteMapping("/roles/{roleName}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    public ApiResponse<Void> deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);
        return ApiResponse.success();
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<List<RoleApiDto.PermissionResp>> getAllPermissions() {
        List<Permission> permissions = roleService.getAllPermissions();
        List<RoleApiDto.PermissionResp> resp = permissions.stream()
                .map(p -> new RoleApiDto.PermissionResp(
                        p.getName(),
                        p.getDescription(),
                        p.getResource(),
                        p.getAction()
                ))
                .collect(Collectors.toList());
        return ApiResponse.success(resp);
    }
}
