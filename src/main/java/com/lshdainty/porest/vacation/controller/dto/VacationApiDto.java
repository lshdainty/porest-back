package com.lshdainty.porest.vacation.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.vacation.type.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VacationApiDto {

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistVacationReq {
        private String userId;
        private String vacationDesc;
        private VacationType vacationType;
        private BigDecimal grantTime;
        private LocalDateTime occurDate;
        private LocalDateTime expiryDate;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistVacationResp {
        private Long vacationId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UseVacationReq {
        private String userId;
        private VacationType vacationType;
        private String vacationDesc;
        private VacationTimeType vacationTimeType;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UseVacationResp {
        private Long vacationUsageId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchUserVacationsResp {
        private List<VacationGrantInfo> grants;  // 부여받은 내역
        private List<VacationUsageInfo> usages;  // 사용한 내역

        @Getter
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class VacationGrantInfo {
            private Long vacationGrantId;
            private VacationType vacationType;
            private String vacationTypeName;
            private String vacationGrantDesc;
            private BigDecimal grantTime;
            private BigDecimal remainTime;
            private LocalDateTime grantDate;
            private LocalDateTime expiryDate;
        }

        @Getter
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class VacationUsageInfo {
            private Long vacationUsageId;
            private String vacationUsageDesc;
            private VacationTimeType vacationTimeType;
            private String vacationTimeTypeName;
            private BigDecimal usedTime;
            private LocalDateTime startDate;
            private LocalDateTime endDate;
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchUserGroupVacationsResp {
        private String userId;
        private String userName;
        private List<VacationGrantInfo> grants;  // 부여받은 내역
        private List<VacationUsageInfo> usages;  // 사용한 내역

        @Getter
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class VacationGrantInfo {
            private Long vacationGrantId;
            private VacationType vacationType;
            private String vacationTypeName;
            private String vacationGrantDesc;
            private BigDecimal grantTime;
            private BigDecimal remainTime;
            private LocalDateTime grantDate;
            private LocalDateTime expiryDate;
        }

        @Getter
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class VacationUsageInfo {
            private Long vacationUsageId;
            private String vacationUsageDesc;
            private VacationTimeType vacationTimeType;
            private String vacationTimeTypeName;
            private BigDecimal usedTime;
            private LocalDateTime startDate;
            private LocalDateTime endDate;
        }
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchAvailableVacationsResp {
        private VacationType vacationType;
        private String vacationTypeName;
        private BigDecimal totalRemainTime;
        private String totalRemainTimeStr;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchPeriodVacationUseHistoriesResp {
        private String userId;
        private String userName;
        private Long vacationUsageId;
        private String vacationUsageDesc;
        private VacationTimeType vacationTimeType;
        private String vacationTimeTypeName;
        private BigDecimal usedTime;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchUserPeriodVacationUseHistoriesResp {
        private Long vacationUsageId;
        private String vacationUsageDesc;
        private VacationTimeType vacationTimeType;
        private String vacationTimeTypeName;
        private BigDecimal usedTime;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchUserMonthStatsVacationUseHistoriesResp {
        private Integer month;
        private BigDecimal usedTime;
        private String usedTimeStr;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchUserVacationUseStatsResp {
        private BigDecimal remainTime;
        private String remainTimeStr;
        private BigDecimal usedTime;
        private String usedTimeStr;
        private BigDecimal expectUsedTime;
        private String expectUsedTimeStr;
        private BigDecimal prevRemainTime;
        private String prevRemainTimeStr;
        private BigDecimal prevUsedTime;
        private String prevUsedTimeStr;
        private BigDecimal prevExpectUsedTime;
        private String prevExpectUsedTimeStr;
        private BigDecimal remainTimeGap;
        private String remainTimeGapStr;
        private BigDecimal usedTimeGap;
        private String usedTimeGapStr;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchVacationPoliciesResp {
        private Long vacationPolicyId;
        private String vacationPolicyName;
        private String vacationPolicyDesc;
        private VacationType vacationType;
        private GrantMethod grantMethod;
        private BigDecimal grantTime;
        private String grantTimeStr;
        private RepeatUnit repeatUnit;
        private Integer repeatInterval;
        private Integer specificMonths;
        private Integer specificDays;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistVacationPolicyReq {
        private String vacationPolicyName;
        private String vacationPolicyDesc;
        private VacationType vacationType;
        private GrantMethod grantMethod;
        private BigDecimal grantTime;
        private RepeatUnit repeatUnit;
        private Integer repeatInterval;
        private Integer specificMonths;
        private Integer specificDays;
        private LocalDateTime firstGrantDate;  // 첫 부여 시점 (반복 부여 방식에서 필수)
        private YNType isRecurring;            // 반복 여부 (Y: 반복, N: 1회)
        private Integer maxGrantCount;         // 최대 부여 횟수 (1회성 정책용)
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistVacationPolicyResp {
        private Long vacationPolicyId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class AssignVacationPoliciesToUserReq {
        private List<Long> vacationPolicyIds;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class AssignVacationPoliciesToUserResp {
        private String userId;
        private List<Long> assignedVacationPolicyIds;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchUserVacationPoliciesResp {
        private Long userVacationPolicyId;
        private Long vacationPolicyId;
        private String vacationPolicyName;
        private String vacationPolicyDesc;
        private VacationType vacationType;
        private GrantMethod grantMethod;
        private BigDecimal grantTime;
        private String grantTimeStr;
        private RepeatUnit repeatUnit;
        private Integer repeatInterval;
        private Integer specificMonths;
        private Integer specificDays;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RevokeVacationPolicyFromUserResp {
        private String userId;
        private Long vacationPolicyId;
        private Long userVacationPolicyId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RevokeVacationPoliciesFromUserReq {
        private List<Long> vacationPolicyIds;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RevokeVacationPoliciesFromUserResp {
        private String userId;
        private List<Long> revokedVacationPolicyIds;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DeleteVacationPolicyResp {
        private Long vacationPolicyId;
    }
}
