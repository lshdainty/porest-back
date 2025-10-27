package com.lshdainty.porest.vacation.service.type;

import com.lshdainty.porest.vacation.service.dto.VacationServiceDto;

public interface VacationTypeStrategy {
    Long registVacation(VacationServiceDto data);
}