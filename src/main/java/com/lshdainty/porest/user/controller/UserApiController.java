package com.lshdainty.porest.user.controller;

import com.lshdainty.porest.user.controller.dto.UserApiDto;
import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.user.service.UserService;
import com.lshdainty.porest.user.service.dto.UserServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserApiController {
    private final UserService userService;

    @PostMapping("/api/v1/user")
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

    @GetMapping("/api/v1/user/{id}")
    public ApiResponse searchUser(@PathVariable("id") String userId) {
        UserServiceDto user = userService.searchUser(userId);

        return ApiResponse.success(new UserApiDto.SearchUserResp(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirth(),
                user.getWorkTime(),
                user.getRole(),
                user.getRole().name(),
                user.getCompany(),
                user.getCompany().getCompanyName(),
                user.getLunarYN(),
                user.getProfileUrl(),
                user.getInvitationToken(),
                user.getInvitationSentAt(),
                user.getInvitationExpiresAt(),
                user.getInvitationStatus(),
                user.getRegisteredAt()
        ));
    }

    @GetMapping("/api/v1/users")
    public ApiResponse searchUsers() {
        List<UserServiceDto> users = userService.searchUsers();

        List<UserApiDto.SearchUserResp> resps = users.stream()
                .map(u -> new UserApiDto.SearchUserResp(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getBirth(),
                        u.getWorkTime(),
                        u.getRole(),
                        u.getRole().name(),
                        u.getCompany(),
                        u.getCompany().getCompanyName(),
                        u.getLunarYN(),
                        u.getProfileUrl(),
                        u.getInvitationToken(),
                        u.getInvitationSentAt(),
                        u.getInvitationExpiresAt(),
                        u.getInvitationStatus(),
                        u.getRegisteredAt()
                ))
                .collect(Collectors.toList());

        return ApiResponse.success(resps);
    }

    @PutMapping("/api/v1/user/{id}")
    public ApiResponse editUser(@PathVariable("id") String userId, @RequestBody UserApiDto.EditUserReq data) {
        userService.editUser(UserServiceDto.builder()
                .id(userId)
                .name(data.getUserName())
                .email(data.getUserEmail())
                .birth(data.getUserBirth())
                .role(data.getUserRoleType())
                .company(data.getUserOriginCompanyType())
                .workTime(data.getUserWorkTime())
                .lunarYN(data.getLunarYn())
                .profileUrl(data.getProfileUrl())
                .profileUUID(data.getProfileUuid())
                .build()
        );

        UserServiceDto findUser = userService.searchUser(userId);

        return ApiResponse.success(new UserApiDto.EditUserResp(
                findUser.getId(),
                findUser.getName(),
                findUser.getEmail(),
                findUser.getBirth(),
                findUser.getWorkTime(),
                findUser.getRole(),
                findUser.getRole().name(),
                findUser.getCompany(),
                findUser.getCompany().getCompanyName(),
                findUser.getLunarYN(),
                findUser.getProfileUrl()
        ));
    }

    @DeleteMapping("/api/v1/user/{id}")
    public ApiResponse deleteUser(@PathVariable("id") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.success();
    }

    @PostMapping(value = "/api/v1/user/upload/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @PostMapping("/api/v1/user/invite")
    public ApiResponse inviteUser(@RequestBody UserApiDto.InviteUserReq data) {
        UserServiceDto result = userService.inviteUser(UserServiceDto.builder()
                .id(data.getUserId())
                .name(data.getUserName())
                .email(data.getUserEmail())
                .company(data.getUserOriginCompanyType())
                .workTime(data.getUserWorkTime())
                .build()
        );

        return ApiResponse.success(new UserApiDto.InviteUserResp(
                result.getId(),
                result.getName(),
                result.getEmail(),
                result.getCompany(),
                result.getWorkTime(),
                result.getRole(),
                result.getInvitationSentAt(),
                result.getInvitationExpiresAt(),
                result.getInvitationStatus()
        ));
    }

    /**
     * 초대 이메일 재전송
     */
    @PostMapping("/api/v1/user/invitation/resend/{id}")
    public ApiResponse resendInvitation(@PathVariable("id") String userId) {
        UserServiceDto result = userService.resendInvitation(userId);

        return ApiResponse.success(new UserApiDto.ResendInvitationResp(
                result.getId(),
                result.getName(),
                result.getEmail(),
                result.getCompany(),
                result.getWorkTime(),
                result.getRole(),
                result.getInvitationSentAt(),
                result.getInvitationExpiresAt(),
                result.getInvitationStatus()
        ));
    }
}
