package com.lshdainty.myhr.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class DuesServiceDto {
    private Long totalDues;
    private Long totalDeposit;
    private Long totalWithdrawal;
}
