package com.lshdainty.myhr.service.vacation;

import com.lshdainty.myhr.domain.User;
import com.lshdainty.myhr.domain.VacationType;
import com.lshdainty.myhr.repository.UserRepositoryImpl;
import com.lshdainty.myhr.repository.VacationHistoryRepositoryImpl;
import com.lshdainty.myhr.repository.VacationRepositoryImpl;
import com.lshdainty.myhr.service.UserService;
import com.lshdainty.myhr.service.VacationService;
import org.springframework.context.MessageSource;
import com.lshdainty.myhr.domain.Vacation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Annual extends VacationService {
    VacationRepositoryImpl vacationRepositoryImpl;
    UserService userService;

    public Annual(
            MessageSource ms,
            VacationRepositoryImpl vacationRepositoryImpl,
            VacationHistoryRepositoryImpl vacationHistoryRepositoryImpl,
            UserRepositoryImpl userRepositoryImpl,
            UserService userService
    ) {
        super(ms, vacationRepositoryImpl, vacationHistoryRepositoryImpl, userRepositoryImpl, userService);
    }

    @Override
    public Long registVacation(Long userNo, String desc, VacationType type, BigDecimal grantTime, LocalDateTime occurDate, LocalDateTime expiryDate, Long addUserNo, String clientIP) {
        User user = userService.checkUserExist(userNo);

        // 올해 등록된 연차 휴가가 있는지 확인
        // 등록된 휴가가 있으면 기존 휴가에 새롭게 부여한 휴가 더하기
        // 히스토리 같이 저장하기
        // 등록된 휴가가 없으면 새롭게 연차 등록하기
        // 히스토리 같이 저장하기

        return 0L;
    }
}
