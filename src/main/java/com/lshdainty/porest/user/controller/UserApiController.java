package com.lshdainty.porest.user.controller;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.user.controller.dto.UserApiDto;
import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.user.service.UserService;
import com.lshdainty.porest.user.service.dto.UserServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserApiController {
    private final UserService userService;

    @PostMapping("/api/v1/users")
    public ApiResponse joinUser(@RequestBody UserApiDto.JoinUserReq data) {
        String userId = userService.joinUser(UserServiceDto.builder()
                .id(data.getUserId())
                .pwd(data.getUserPwd())
                .name(data.getUserName())
                .email(data.getUserEmail())
                .birth(data.getUserBirth())
                .company(data.getUserOriginCompanyType())
                .workTime(data.getUserWorkTime())
                .lunarYN(data.getLunarYn())
                .profileUrl(data.getProfileUrl())
                .profileUUID(data.getProfileUuid())
                .build()
        );

        return ApiResponse.success(new UserApiDto.JoinUserResp(userId));
    }

    @GetMapping("/api/v1/users/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse searchUser(@PathVariable("id") String userId) {
        UserServiceDto user = userService.searchUser(userId);

        return ApiResponse.success(new UserApiDto.SearchUserResp(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirth(),
                user.getWorkTime(),
                user.getJoinDate(),
                user.getRoleNames(),
                user.getRoleNames().isEmpty() ? null : user.getRoleNames().get(0),
                user.getCompany(),
                user.getCompany().getCompanyName(),
                user.getLunarYN(),
                user.getProfileUrl(),
                user.getInvitationToken(),
                user.getInvitationSentAt(),
                user.getInvitationExpiresAt(),
                user.getInvitationStatus(),
                user.getRegisteredAt(),
                user.getMainDepartmentNameKR(),
                user.getDashboard()
        ));
    }

    @GetMapping("/api/v1/users/check-duplicate")
    public ApiResponse checkUserIdDuplicate(@RequestParam("user_id") String userId) {
        boolean isDuplicate = userService.checkUserIdDuplicate(userId);
        return ApiResponse.success(new UserApiDto.CheckUserIdDuplicateResp(isDuplicate));
    }

    @GetMapping("/api/v1/users")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse searchUsers() {
        List<UserServiceDto> users = userService.searchUsers();

        List<UserApiDto.SearchUserResp> resps = users.stream()
                .map(u -> new UserApiDto.SearchUserResp(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getBirth(),
                        u.getWorkTime(),
                        u.getJoinDate(),
                        u.getRoleNames(),
                        u.getRoleNames().isEmpty() ? null : u.getRoleNames().get(0),
                        u.getCompany(),
                        u.getCompany().getCompanyName(),
                        u.getLunarYN(),
                        u.getProfileUrl(),
                        u.getInvitationToken(),
                        u.getInvitationSentAt(),
                        u.getInvitationExpiresAt(),
                        u.getInvitationStatus(),
                        u.getRegisteredAt(),
                        u.getMainDepartmentNameKR(),
                        u.getDashboard()
                ))
                .collect(Collectors.toList());

        return ApiResponse.success(resps);
    }

    @PutMapping("/api/v1/users/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ApiResponse editUser(@PathVariable("id") String userId, @RequestBody UserApiDto.EditUserReq data) {
        userService.editUser(UserServiceDto.builder()
                .id(userId)
                .name(data.getUserName())
                .email(data.getUserEmail())
                .birth(data.getUserBirth())
                .roleNames(data.getUserRoles())
                .company(data.getUserOriginCompanyType())
                .workTime(data.getUserWorkTime())
                .lunarYN(data.getLunarYn())
                .profileUrl(data.getProfileUrl())
                .profileUUID(data.getProfileUuid())
                .dashboard(data.getDashboard())
                .build()
        );

        UserServiceDto findUser = userService.searchUser(userId);

        return ApiResponse.success(new UserApiDto.EditUserResp(
                findUser.getId(),
                findUser.getName(),
                findUser.getEmail(),
                findUser.getBirth(),
                findUser.getWorkTime(),
                findUser.getRoleNames(),
                findUser.getRoleNames().isEmpty() ? null : findUser.getRoleNames().get(0),
                findUser.getCompany(),
                findUser.getCompany().getCompanyName(),
                findUser.getLunarYN(),
                findUser.getProfileUrl(),
                findUser.getDashboard()
        ));
    }

    @DeleteMapping("/api/v1/users/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ApiResponse deleteUser(@PathVariable("id") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.success();
    }

    @PostMapping(value = "/api/v1/users/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse uploadProfile(@ModelAttribute UserApiDto.UploadProfileReq data) {
        UserServiceDto dto = userService.saveProfileImgInTempFolder(data.getProfile());
        return ApiResponse.success(new UserApiDto.UploadProfileResp(
                dto.getProfileUrl(),
                dto.getProfileUUID()
        ));
    }

    /**
     * 관리자가 사용자 초대
     */
    @PostMapping("/api/v1/users/invitations")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ApiResponse inviteUser(@RequestBody UserApiDto.InviteUserReq data) {
        UserServiceDto result = userService.inviteUser(UserServiceDto.builder()
                .id(data.getUserId())
                .name(data.getUserName())
                .email(data.getUserEmail())
                .company(data.getUserOriginCompanyType())
                .workTime(data.getUserWorkTime())
                .joinDate(data.getJoinDate())
                .build()
        );

        return ApiResponse.success(new UserApiDto.InviteUserResp(
                result.getId(),
                result.getName(),
                result.getEmail(),
                result.getCompany(),
                result.getWorkTime(),
                result.getJoinDate(),
                result.getRoleNames(),
                result.getInvitationSentAt(),
                result.getInvitationExpiresAt(),
                result.getInvitationStatus()
        ));
    }

    /**
     * 초대된 사용자 정보 수정
     */
    @PutMapping("/api/v1/users/{id}/invitations")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ApiResponse editInvitedUser(@PathVariable("id") String userId, @RequestBody UserApiDto.EditInvitedUserReq data) {
        UserServiceDto result = userService.editInvitedUser(userId, UserServiceDto.builder()
                .name(data.getUserName())
                .email(data.getUserEmail())
                .company(data.getUserOriginCompanyType())
                .workTime(data.getUserWorkTime())
                .joinDate(data.getJoinDate())
                .build()
        );

        return ApiResponse.success(new UserApiDto.EditInvitedUserResp(
                result.getId(),
                result.getName(),
                result.getEmail(),
                result.getCompany(),
                result.getWorkTime(),
                result.getJoinDate(),
                result.getRoleNames(),
                result.getInvitationSentAt(),
                result.getInvitationExpiresAt(),
                result.getInvitationStatus()
        ));
    }

    /**
     * 초대 이메일 재전송
     */
    @PostMapping("/api/v1/users/{id}/invitations/resend")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ApiResponse resendInvitation(@PathVariable("id") String userId) {
        UserServiceDto result = userService.resendInvitation(userId);

        return ApiResponse.success(new UserApiDto.ResendInvitationResp(
                result.getId(),
                result.getName(),
                result.getEmail(),
                result.getCompany(),
                result.getWorkTime(),
                result.getJoinDate(),
                result.getRoleNames(),
                result.getInvitationSentAt(),
                result.getInvitationExpiresAt(),
                result.getInvitationStatus()
        ));
    }

    /**
     * 사용자의 메인 부서 존재 여부 확인
     */
    @GetMapping("/api/v1/users/{userId}/main-department/existence")
    public ApiResponse checkUserMainDepartmentExistence(@PathVariable("userId") String userId) {
        YNType hasMainDepartment = userService.checkUserHasMainDepartment(userId);
        return ApiResponse.success(new UserApiDto.CheckMainDepartmentExistenceResp(hasMainDepartment));
    }

    /**
     * 유저 대시보드 수정
     * PATCH /api/v1/users/{userId}/dashboard
     */
    @PatchMapping("/api/v1/users/{userId}/dashboard")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ApiResponse updateDashboard(@PathVariable("userId") String userId, @RequestBody UserApiDto.UpdateDashboardReq data) {
        UserServiceDto result = userService.updateDashboard(userId, data.getDashboard());

        return ApiResponse.success(new UserApiDto.UpdateDashboardResp(
                result.getId(),
                result.getDashboard()
        ));
    }

    /**
     * 특정 유저의 승인권자 목록 조회
     * GET /api/v1/users/{userId}/approvers
     */
    @GetMapping("/api/v1/users/{userId}/approvers")
    public ApiResponse getUserApprovers(@PathVariable("userId") String userId) {
        List<UserServiceDto> approvers = userService.getUserApprovers(userId);

        List<UserApiDto.GetApproversResp> resp = approvers.stream()
                .map(approver -> new UserApiDto.GetApproversResp(
                        approver.getId(),
                        approver.getName(),
                        approver.getEmail(),
                        approver.getRoleNames(),
                        approver.getRoleNames().isEmpty() ? null : approver.getRoleNames().get(0),
                        approver.getDepartmentId(),
                        approver.getDepartmentName(),
                        approver.getDepartmentNameKR(),
                        approver.getDepartmentLevel()
                ))
                .collect(Collectors.toList());

        return ApiResponse.success(resp);
    }
}
