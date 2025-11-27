package com.lshdainty.porest.work.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lshdainty.porest.work.type.CodeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WorkCodeApiDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CreateWorkCodeReq {
        private String workCode;
        private String workCodeName;
        private CodeType codeType;
        private Long parentWorkCodeSeq;
        private Integer orderSeq;
    }

    @Getter
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CreateWorkCodeResp {
        private Long workCodeSeq;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UpdateWorkCodeReq {
        private String workCode;
        private String workCodeName;
        private Long parentWorkCodeSeq;
        private Integer orderSeq;
    }
}
