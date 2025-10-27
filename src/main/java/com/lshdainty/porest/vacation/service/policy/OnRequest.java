package com.lshdainty.porest.vacation.service.policy;

import com.lshdainty.porest.vacation.domain.VacationPolicy;
import com.lshdainty.porest.vacation.repository.VacationPolicyCustomRepositoryImpl;
import com.lshdainty.porest.vacation.service.dto.VacationPolicyServiceDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OnRequest implements VacationPolicyStrategy {
    private final VacationPolicyCustomRepositoryImpl vacationPolicyRepository;

    @Override
    public Long registVacationPolicy(VacationPolicyServiceDto data) {
        // 반복 단위, 반복 간격, 부여시점 지정 방식, 특정월, 특정일을 모두 null로 설정하여 강제 저장
        // 직접 신청하는 휴가의 경우 스케줄러가 필요없음
        VacationPolicy vacationPolicy = VacationPolicy.createVacationPolicy(
                data.getName(),
                data.getDesc(),
                data.getVacationType(),
                data.getGrantMethod(),
                data.getGrantTime(),
                null,
                null,
                null,
                null,
                null
        );

        vacationPolicyRepository.save(vacationPolicy);
        return vacationPolicy.getId();
    }
}
