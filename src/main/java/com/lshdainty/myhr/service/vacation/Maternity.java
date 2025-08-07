package com.lshdainty.myhr.service.vacation;

import com.lshdainty.myhr.domain.User;
import com.lshdainty.myhr.domain.Vacation;
import com.lshdainty.myhr.domain.VacationHistory;
import com.lshdainty.myhr.repository.HolidayRepositoryImpl;
import com.lshdainty.myhr.repository.UserRepositoryImpl;
import com.lshdainty.myhr.repository.VacationHistoryRepositoryImpl;
import com.lshdainty.myhr.repository.VacationRepositoryImpl;
import com.lshdainty.myhr.service.UserService;
import com.lshdainty.myhr.service.VacationService;
import com.lshdainty.myhr.service.dto.VacationServiceDto;
import org.springframework.context.MessageSource;

public class Maternity extends VacationService {
    MessageSource ms;
    VacationRepositoryImpl vacationRepositoryImpl;
    VacationHistoryRepositoryImpl vacationHistoryRepositoryImpl;
    UserRepositoryImpl userRepositoryImpl;
    HolidayRepositoryImpl holidayRepositoryImpl;
    UserService userService;

    public Maternity(
            MessageSource ms,
            VacationRepositoryImpl vacationRepositoryImpl,
            VacationHistoryRepositoryImpl vacationHistoryRepositoryImpl,
            UserRepositoryImpl userRepositoryImpl,
            HolidayRepositoryImpl holidayRepositoryImpl,
            UserService userService
    ) {
        super(ms, vacationRepositoryImpl, vacationHistoryRepositoryImpl, userRepositoryImpl, holidayRepositoryImpl, userService);
        this.ms = ms;
        this.vacationRepositoryImpl = vacationRepositoryImpl;
        this.vacationHistoryRepositoryImpl = vacationHistoryRepositoryImpl;
        this.userRepositoryImpl = userRepositoryImpl;
        this.holidayRepositoryImpl = holidayRepositoryImpl;
        this.userService = userService;
    }

    @Override
    public Long registVacation(VacationServiceDto data, String crtUserId, String clientIP) {
        User user = userService.checkUserExist(data.getUserId());

        Vacation vacation = Vacation.createVacation(
                user,
                data.getType(),
                data.getGrantTime(),
                data.getOccurDate(),
                data.getExpiryDate(),
                crtUserId,
                clientIP
        );
        vacationRepositoryImpl.save(vacation);

        VacationHistory history = VacationHistory.createRegistVacationHistory(vacation, data.getDesc(), data.getGrantTime(), crtUserId, clientIP);
        vacationHistoryRepositoryImpl.save(history);

        return vacation.getId();
    }
}
