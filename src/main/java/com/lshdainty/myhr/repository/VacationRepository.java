package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.Vacation;

import java.time.LocalDateTime;
import java.util.List;

public interface VacationRepository {
    void save(Vacation vacation);
    Vacation findById(Long vacationId);
    List<Vacation> findVacationsByUserNo(Long userNo);
    List<Vacation> findVacationsByYear(String year);
    List<Vacation> findVacationByParameterTime(Long userNo, LocalDateTime standardTime);
}
