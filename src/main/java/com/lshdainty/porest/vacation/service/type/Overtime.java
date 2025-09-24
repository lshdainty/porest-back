package com.lshdainty.porest.vacation.service.type;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.vacation.domain.Vacation;
import com.lshdainty.porest.vacation.domain.VacationHistory;
import com.lshdainty.porest.vacation.repository.VacationHistoryRepositoryImpl;
import com.lshdainty.porest.vacation.repository.VacationRepositoryImpl;
import com.lshdainty.porest.user.service.UserService;
import com.lshdainty.porest.vacation.service.VacationService;
import com.lshdainty.porest.vacation.service.dto.VacationServiceDto;

import java.time.LocalDateTime;
import java.util.Optional;

public class Overtime extends VacationService {
    VacationRepositoryImpl vacationRepository;
    VacationHistoryRepositoryImpl vacationHistoryRepository;
    UserService userService;

    public Overtime(
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
    public Long registVacation(VacationServiceDto data, String crtUserId, String clientIP) {
        User user = userService.checkUserExist(data.getUserId());

        Optional<Vacation> vacation = vacationRepository.findVacationByTypeWithYear(data.getUserId(), data.getType(), String.valueOf(data.getOccurDate().getYear()));
        if (vacation.isPresent()) {
            vacation.get().addVacation(data.getGrantTime(), crtUserId, clientIP);
        } else {
            // 보상연차의 경우 당해년도 1월 1일부터 12월 31일로 고정 생성
            Vacation newVacation = Vacation.createVacation(
                    user,
                    data.getType(),
                    data.getGrantTime(),
                    LocalDateTime.of(data.getOccurDate().getYear(), 1, 1, 0, 0, 0),
                    LocalDateTime.of(data.getOccurDate().getYear(), 12, 31, 23, 59, 59),
                    crtUserId,
                    clientIP
            );
            vacationRepository.save(newVacation);
            vacation = Optional.of(newVacation);
        }

        VacationHistory history = VacationHistory.createRegistVacationHistory(vacation.get(), data.getDesc(), data.getGrantTime(), crtUserId, clientIP);
        vacationHistoryRepository.save(history);

        return vacation.get().getId();
    }
}
