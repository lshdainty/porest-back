package com.lshdainty.porest.vacation.service.type;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.vacation.domain.Vacation;
import com.lshdainty.porest.vacation.domain.VacationHistory;
import com.lshdainty.porest.vacation.repository.VacationHistoryRepositoryImpl;
import com.lshdainty.porest.vacation.repository.VacationRepositoryImpl;
import com.lshdainty.porest.user.service.UserService;
import com.lshdainty.porest.vacation.service.VacationService;
import com.lshdainty.porest.vacation.service.dto.VacationServiceDto;

public class Bereavement extends VacationService {
    VacationRepositoryImpl vacationRepository;
    VacationHistoryRepositoryImpl vacationHistoryRepository;
    UserService userService;

    public Bereavement(
            VacationRepositoryImpl vacationRepository,
            VacationHistoryRepositoryImpl vacationHistoryRepository,
            UserService userService
    ) {
        super(null, vacationRepository, vacationHistoryRepository, null, null, null, userService);
        this.vacationRepository = vacationRepository;
        this.vacationHistoryRepository = vacationHistoryRepository;
        this.userService = userService;
    }

    @Override
    public Long registVacation(VacationServiceDto data) {
        User user = userService.checkUserExist(data.getUserId());

        Vacation vacation = Vacation.createVacation(
                user,
                data.getType(),
                data.getGrantTime(),
                data.getOccurDate(),
                data.getExpiryDate()
        );
        vacationRepository.save(vacation);

        VacationHistory history = VacationHistory.createRegistVacationHistory(vacation, data.getDesc(), data.getGrantTime());
        vacationHistoryRepository.save(history);

        return vacation.getId();
    }
}
