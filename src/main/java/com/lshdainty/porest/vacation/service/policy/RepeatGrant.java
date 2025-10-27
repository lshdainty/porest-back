package com.lshdainty.porest.vacation.service.policy;

import com.lshdainty.porest.vacation.domain.VacationPolicy;
import com.lshdainty.porest.vacation.repository.VacationPolicyCustomRepositoryImpl;
import com.lshdainty.porest.vacation.service.dto.VacationPolicyServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Objects;

@RequiredArgsConstructor
public class RepeatGrant implements VacationPolicyStrategy {
    private final MessageSource ms;
    private final VacationPolicyCustomRepositoryImpl vacationPolicyRepository;

    @Override
    public Long registVacationPolicy(VacationPolicyServiceDto data) {
        // 반복 휴가 정책의 경우 반복에 대한 값이 정확하게 설정되어야 한다.
        // 부여 시간이 null인 경우 에러 반환(스케줄러에서 휴가 부여 불가능
        if (Objects.isNull(data.getGrantTime())) {
            throw new IllegalArgumentException(ms.getMessage("", null, null));
        }

        VacationPolicy vacationPolicy = VacationPolicy.createVacationPolicy(
                data.getName(),
                data.getDesc(),
                data.getVacationType(),
                data.getGrantMethod(),
                data.getGrantTime(),
                data.getRepeatUnit(),
                data.getRepeatInterval(),
                data.getGrantTiming(),
                data.getSpecificMonths(),
                data.getSpecificDays()
        );

        vacationPolicyRepository.save(vacationPolicy);
        return vacationPolicy.getId();
    }
}
