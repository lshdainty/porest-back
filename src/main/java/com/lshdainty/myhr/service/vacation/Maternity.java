package com.lshdainty.myhr.service.vacation;

import com.lshdainty.myhr.domain.VacationType;
import com.lshdainty.myhr.repository.UserRepositoryImpl;
import com.lshdainty.myhr.repository.VacationHistoryRepositoryImpl;
import com.lshdainty.myhr.repository.VacationRepositoryImpl;
import com.lshdainty.myhr.service.UserService;
import com.lshdainty.myhr.service.VacationService;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Maternity extends VacationService {
    public Maternity(
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
        return 0L;
    }
}
