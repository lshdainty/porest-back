package com.lshdainty.porest.department.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class DepartmentApiDto {
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistDepartmentReq {
        private String departmentName;
        private String departmentNameKr;
        private Long parentId;
        private String headUserId;
        private Long treeLevel;
        private String departmentDesc;
        private String colorCode;
        private String companyId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistDepartmentResp {
        private Long departmentId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditDepartmentReq {
        private String departmentName;
        private String departmentNameKr;
        private Long parentId;
        private String headUserId;
        private Long treeLevel;
        private String departmentDesc;
        private String colorCode;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchDepartmentResp {
        private Long departmentId;
        private String departmentName;
        private String departmentNameKr;
        private Long parentId;
        private String headUserId;
        private Long treeLevel;
        private String departmentDesc;
        private String colorCode;
        private String companyId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchDepartmentWithChildrenResp {
        private Long departmentId;
        private String departmentName;
        private String departmentNameKr;
        private Long parentId;
        private String headUserId;
        private Long treeLevel;
        private String departmentDesc;
        private String colorCode;
        private String companyId;
        private List<SearchDepartmentWithChildrenResp> children;
    }
}