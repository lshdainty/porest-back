package com.lshdainty.porest.dues.service.dto;

import com.lshdainty.porest.dues.type.DuesCalcType;
import com.lshdainty.porest.dues.type.DuesType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class DuesServiceDto {
    private Long seq;
    private String userName;
    private Long amount;
    private DuesType type;
    private DuesCalcType calc;
    private String date;
    private String detail;

    private Long totalDues;
    private Long totalDeposit;
    private Long totalWithdrawal;
}