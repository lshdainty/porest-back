package com.lshdainty.porest.company.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.porest.department.controller.dto.DepartmentApiDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class CompanyApiDto {
    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistCompanyReq {
        private String companyId;
        private String companyName;
        private String companyDesc;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RegistCompanyResp {
        private String companyId;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchCompanyResp {
        private String companyId;
        private String companyName;
        private String companyDesc;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditCompanyReq {
        private String companyName;
        private String companyDesc;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SearchCompanyWithDepartmentsResp {
        private String companyId;
        private String companyName;
        private String companyDesc;
        private List<DepartmentApiDto.SearchDepartmentWithChildrenResp> departments;
    }
}
