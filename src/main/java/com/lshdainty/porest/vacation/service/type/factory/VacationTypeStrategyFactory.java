package com.lshdainty.porest.vacation.service.type.factory;

import com.lshdainty.porest.user.service.UserService;
import com.lshdainty.porest.vacation.repository.VacationHistoryRepositoryImpl;
import com.lshdainty.porest.vacation.repository.VacationRepositoryImpl;
import com.lshdainty.porest.vacation.service.type.*;
import com.lshdainty.porest.vacation.type.VacationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacationTypeStrategyFactory {
    private final MessageSource ms;
    private final VacationRepositoryImpl vacationRepository;
    private final VacationHistoryRepositoryImpl vacationHistoryRepository;
    private final UserService userService;

    public VacationTypeStrategy getStrategy(VacationType vacationType) {
        return switch (vacationType) {
            case ANNUAL -> new Annual(vacationRepository, vacationHistoryRepository, userService);
            case MATERNITY -> new Maternity(vacationRepository, vacationHistoryRepository, userService);
            case WEDDING -> new Wedding(vacationRepository, vacationHistoryRepository, userService);
            case BEREAVEMENT -> new Bereavement(vacationRepository, vacationHistoryRepository, userService);
            case OVERTIME -> new Overtime(vacationRepository, vacationHistoryRepository, userService);
            default -> throw new IllegalArgumentException(ms.getMessage("error.notfound.vacationtype", null, null));
        };
    }
}