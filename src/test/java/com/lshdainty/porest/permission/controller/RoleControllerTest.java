package com.lshdainty.porest.permission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.permission.controller.dto.RoleApiDto;
import com.lshdainty.porest.permission.domain.Permission;
import com.lshdainty.porest.permission.domain.Role;
import com.lshdainty.porest.permission.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/roles - 모든 역할 조회 성공")
    @WithMockUser(authorities = "ROLE_READ")
    void getAllRolesSuccess() throws Exception {
        // given
        Role role = Role.createRole("ADMIN", "관리자");
        given(roleService.getAllRoles()).willReturn(List.of(role));

        // when & then
        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].roleName").value("ADMIN"))
                .andExpect(jsonPath("$.data[0].description").value("관리자"));
    }

    @Test
    @DisplayName("GET /api/v1/roles/{roleName} - 특정 역할 조회 성공")
    @WithMockUser(authorities = "ROLE_READ")
    void getRoleSuccess() throws Exception {
        // given
        Role role = Role.createRole("ADMIN", "관리자");
        Permission permission = Permission.create("USER_READ", "유저 조회", "USER", "READ");
        role.addPermission(permission);
        given(roleService.getRole("ADMIN")).willReturn(role);

        // when & then
        mockMvc.perform(get("/api/v1/roles/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleName").value("ADMIN"))
                .andExpect(jsonPath("$.data.permissions[0].name").value("USER_READ"));
    }

    @Test
    @DisplayName("POST /api/v1/roles - 역할 생성 성공")
    @WithMockUser(authorities = "ROLE_MANAGE")
    void createRoleSuccess() throws Exception {
        // given
        RoleApiDto.CreateRoleReq req = new RoleApiDto.CreateRoleReq();
        req.setRoleName("NEW_ROLE");
        req.setDescription("새로운 역할");

        Role role = Role.createRole("NEW_ROLE", "새로운 역할");
        given(roleService.createRole("NEW_ROLE", "새로운 역할")).willReturn(role);

        // when & then
        mockMvc.perform(post("/api/v1/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleName").value("NEW_ROLE"));
    }

    @Test
    @DisplayName("PUT /api/v1/roles/{roleName}/permissions - 역할 권한 수정 성공")
    @WithMockUser(authorities = "ROLE_MANAGE")
    void updateRolePermissionsSuccess() throws Exception {
        // given
        RoleApiDto.UpdateRolePermissionsReq req = new RoleApiDto.UpdateRolePermissionsReq();
        req.setPermissionNames(List.of("USER_READ", "USER_WRITE"));

        Role role = Role.createRole("ADMIN", "관리자");
        given(roleService.updateRolePermissions(eq("ADMIN"), any())).willReturn(role);

        // when & then
        mockMvc.perform(put("/api/v1/roles/ADMIN/permissions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
        
        verify(roleService).updateRolePermissions(eq("ADMIN"), eq(List.of("USER_READ", "USER_WRITE")));
    }

    @Test
    @DisplayName("DELETE /api/v1/roles/{roleName} - 역할 삭제 성공")
    @WithMockUser(authorities = "ROLE_MANAGE")
    void deleteRoleSuccess() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/roles/ADMIN")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(roleService).deleteRole("ADMIN");
    }

    @Test
    @DisplayName("GET /api/v1/permissions - 모든 권한 조회 성공")
    @WithMockUser(authorities = "ROLE_READ")
    void getAllPermissionsSuccess() throws Exception {
        // given
        Permission permission = Permission.create("USER_READ", "유저 조회", "USER", "READ");
        given(roleService.getAllPermissions()).willReturn(List.of(permission));

        // when & then
        mockMvc.perform(get("/api/v1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("USER_READ"))
                .andExpect(jsonPath("$.data[0].resource").value("USER"))
                .andExpect(jsonPath("$.data[0].action").value("READ"));
    }
}
