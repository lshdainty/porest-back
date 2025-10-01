package com.lshdainty.porest.user.type;

import com.lshdainty.porest.common.type.DisplayType;

public enum StatusType implements DisplayType {
    PENDING("가입 대기"), // 초대 후 아직 회원가입하지 않은 상태
    ACTIVE("가입 완료"),  // 회원가입 완료한 상태
    INACTIVE("비활성"),   // 비활성화된 상태
    EXPIRED("토큰 만료"); // 초대 링크가 만료된 상태

    private String strName;

    StatusType(String strName) {
        this.strName = strName;
    }

    public String getViewName() {
        return strName;
    }
}
