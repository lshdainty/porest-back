package com.lshdainty.porest.user.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.company.type.OriginCompanyType;
import com.lshdainty.porest.user.type.StatusType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserApiDto {
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class JoinUserReq {
        private String userId;
        private String userPwd;
        private String userName;
        private String userEmail;
        private LocalDate userBirth;
        private OriginCompanyType userOriginCompanyType;
        private String userWorkTime;
        private YNType lunarYn;
        private String profileUrl;
        private String profileUuid;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class JoinUserResp {
        private String userId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchUserResp {
        private String userId;
        private String userName;
        private String userEmail;
        private LocalDate userBirth;
        private String userWorkTime;
        private LocalDate joinDate;
        private List<RoleDetailResp> roles;          // 역할 상세 정보 (역할 코드, 이름, 권한 목록)
        private List<String> userRoles;              // 역할 이름 목록 (기존 호환성)
        private String userRoleName;                 // 첫 번째 역할 이름 (기존 호환성)
        private List<String> permissions;            // 모든 권한 코드 목록
        private OriginCompanyType userOriginCompanyType;
        private String userOriginCompanyName;
        private YNType lunarYn;
        private String profileUrl;
        private String invitationToken;
        private LocalDateTime invitationSentAt;
        private LocalDateTime invitationExpiresAt;
        private StatusType invitationStatus;
        private LocalDateTime registeredAt;

        private String mainDepartmentNameKr;
        private String dashboard;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditUserReq {
        private String userName;
        private String userEmail;
        private LocalDate userBirth;
        private List<String> userRoles;
        private OriginCompanyType userOriginCompanyType;
        private String userWorkTime;
        private YNType lunarYn;
        private String profileUrl;

        private String profileUuid;
        private String dashboard;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditUserResp {
        private String userId;
        private String userName;
        private String userEmail;
        private LocalDate userBirth;
        private String userWorkTime;
        private List<RoleDetailResp> roles;          // 역할 상세 정보 (역할 코드, 이름, 권한 목록)
        private List<String> userRoles;              // 역할 이름 목록 (기존 호환성)
        private String userRoleName;                 // 첫 번째 역할 이름 (기존 호환성)
        private List<String> permissions;            // 모든 권한 코드 목록
        private OriginCompanyType userOriginCompanyType;
        private String userOriginCompanyName;
        private YNType lunarYn;

        private String profileUrl;
        private String dashboard;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UploadProfileReq {
        private MultipartFile profile;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UploadProfileResp {
        private String profileUrl;
        private String profileUuid;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class InviteUserReq {
        private String userId;
        private String userName;
        private String userEmail;
        private OriginCompanyType userOriginCompanyType;
        private String userWorkTime;
        private LocalDate joinDate;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class InviteUserResp {
        private String userId;
        private String userName;
        private String userEmail;
        private OriginCompanyType userOriginCompanyType;
        private String userWorkTime;
        private LocalDate joinDate;
        private List<String> userRoles;
        private LocalDateTime invitationSentAt;
        private LocalDateTime invitationExpiresAt;
        private StatusType invitationStatus;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ResendInvitationResp {
        private String userId;
        private String userName;
        private String userEmail;
        private OriginCompanyType userOriginCompanyType;
        private String userWorkTime;
        private LocalDate joinDate;
        private List<String> userRoles;
        private LocalDateTime invitationSentAt;
        private LocalDateTime invitationExpiresAt;
        private StatusType invitationStatus;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditInvitedUserReq {
        private String userName;
        private String userEmail;
        private OriginCompanyType userOriginCompanyType;
        private String userWorkTime;
        private LocalDate joinDate;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditInvitedUserResp {
        private String userId;
        private String userName;
        private String userEmail;
        private OriginCompanyType userOriginCompanyType;
        private String userWorkTime;
        private LocalDate joinDate;
        private List<String> userRoles;
        private LocalDateTime invitationSentAt;
        private LocalDateTime invitationExpiresAt;
        private StatusType invitationStatus;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CheckUserIdDuplicateResp {
        private boolean duplicate;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CheckMainDepartmentExistenceResp {
        private YNType hasMainDepartment;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class GetApproversResp {
        private String userId;
        private String userName;
        private String userEmail;
        private List<RoleDetailResp> roles;          // 역할 상세 정보 (역할 코드, 이름, 권한 목록)
        private List<String> userRoles;              // 역할 이름 목록 (기존 호환성)
        private String userRoleName;                 // 첫 번째 역할 이름 (기존 호환성)
        private List<String> permissions;            // 모든 권한 코드 목록
        private Long departmentId;
        private String departmentName;
        private String departmentNameKr;
        private Long departmentLevel;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UpdateDashboardReq {
        private String dashboard;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UpdateDashboardResp {
        private String userId;
        private String dashboard;
    }

    /**
     * 역할 상세 정보 DTO
     */
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RoleDetailResp {
        private String roleCode;                     // 역할 코드 (예: ADMIN, MANAGER)
        private String roleName;                     // 역할 이름 (예: 관리자, 매니저)
        private List<PermissionDetailResp> permissions; // 해당 역할의 권한 목록
    }

    /**
     * 권한 상세 정보 DTO
     */
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class PermissionDetailResp {
        private String permissionCode;               // 권한 코드 (예: USER:READ, VACATION:APPROVE)
        private String permissionName;               // 권한 이름 (예: 사용자 조회, 휴가 승인)
    }
}