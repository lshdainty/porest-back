package com.lshdainty.myhr.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class DuesServiceDto {
    private int totalPrice;
    private int totalDeposit;
    private int totalWithdrawal;
}
