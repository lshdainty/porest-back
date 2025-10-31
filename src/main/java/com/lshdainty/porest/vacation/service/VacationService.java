package com.lshdainty.porest.vacation.service;

import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.holiday.repository.HolidayRepositoryImpl;
import com.lshdainty.porest.user.service.UserService;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.vacation.domain.*;
import com.lshdainty.porest.vacation.repository.*;
import com.lshdainty.porest.vacation.service.dto.VacationPolicyServiceDto;
import com.lshdainty.porest.vacation.service.dto.VacationServiceDto;
import com.lshdainty.porest.vacation.service.policy.VacationPolicyStrategy;
import com.lshdainty.porest.vacation.service.policy.factory.VacationPolicyStrategyFactory;
import com.lshdainty.porest.holiday.type.HolidayType;
import com.lshdainty.porest.vacation.type.VacationTimeType;
import com.lshdainty.porest.vacation.type.VacationType;
import com.lshdainty.porest.common.util.PorestTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VacationService {
    private final MessageSource ms;
    private final VacationPolicyCustomRepositoryImpl vacationPolicyRepository;
    private final UserVacationPolicyCustomRepositoryImpl userVacationPolicyRepository;
    private final HolidayRepositoryImpl holidayRepository;
    private final UserService userService;
    private final VacationPolicyStrategyFactory vacationPolicyStrategyFactory;
    private final VacationGrantCustomRepositoryImpl vacationGrantRepository;
    private final VacationUsageCustomRepositoryImpl vacationUsageRepository;
    private final VacationUsageDeductionCustomRepositoryImpl vacationUsageDeductionRepository;

    @Transactional
    public Long useVacation(VacationServiceDto data) {
        // 1. 사용자 검증
        User user = userService.checkUserExist(data.getUserId());

        // 2. 시작, 종료시간 비교
        if (PorestTime.isAfterThanEndDate(data.getStartDate(), data.getEndDate())) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.startIsAfterThanEnd", null, null));
        }

        // 3. 연차가 아닌 시간단위 휴가인 경우 유연근무제 시간 체크
        if (!data.getTimeType().equals(VacationTimeType.DAYOFF)) {
            if (!user.isBetweenWorkTime(data.getStartDate().toLocalTime(), data.getEndDate().toLocalTime())) {
                throw new IllegalArgumentException(ms.getMessage("error.validate.worktime.startEndTime", null, null));
            }
        }

        // 4. 주말 리스트 조회
        List<LocalDate> weekDays = PorestTime.getBetweenDatesByDayOfWeek(data.getStartDate(), data.getEndDate(), new int[]{6, 7}, ms);

        // 5. 공휴일 리스트 조회
        List<LocalDate> holidays = holidayRepository.findHolidaysByStartEndDateWithType(
                data.getStartDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                data.getEndDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                HolidayType.PUBLIC
        ).stream()
                .map(h -> LocalDate.parse(h.getDate(), DateTimeFormatter.BASIC_ISO_DATE))
                .toList();

        weekDays = PorestTime.addAllDates(weekDays, holidays);

        // 6. 두 날짜 간 모든 날짜 가져오기
        List<LocalDate> betweenDates = PorestTime.getBetweenDates(data.getStartDate(), data.getEndDate(), ms);
        log.info("betweenDates : {}, weekDays : {}", betweenDates, weekDays);

        // 7. 사용자가 캘린더에서 선택한 날짜 중 휴일, 공휴일 제거
        betweenDates = PorestTime.removeAllDates(betweenDates, weekDays);
        log.info("remainDays : {}", betweenDates);

        // 8. 등록하려는 총 사용시간 계산
        BigDecimal totalUseTime = new BigDecimal("0.0000").add(data.getTimeType().convertToValue(betweenDates.size()));

        // 9. 사용 가능한 VacationGrant 조회 (FIFO: VacationType 일치 + 휴가 시작일이 유효기간 내 + 만료일 가까운 순)
        List<VacationGrant> availableGrants = vacationGrantRepository.findAvailableGrantsByUserIdAndTypeAndDate(
                data.getUserId(),
                data.getType(),
                data.getStartDate()  // 사용자가 사용하려는 휴가 시작일
        );

        // 10. 총 잔여 시간 계산 및 검증
        BigDecimal totalRemainTime = availableGrants.stream()
                .map(VacationGrant::getRemainTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalRemainTime.compareTo(totalUseTime) < 0) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.notEnoughRemainTime", null, null));
        }

        // 11. 통합 기간 휴가 사용 내역 생성
        VacationUsage usage = VacationUsage.createVacationUsage(
                user,
                data.getDesc(),
                data.getTimeType(),
                data.getStartDate(),
                data.getEndDate(),
                totalUseTime
        );

        // 12. FIFO로 VacationGrant에서 차감
        List<VacationUsageDeduction> deductionsToSave = new ArrayList<>();
        BigDecimal remainingNeedTime = totalUseTime;

        for (VacationGrant grant : availableGrants) {
            if (remainingNeedTime.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            // 이 grant에서 차감 가능한 시간
            BigDecimal deductibleTime = grant.getRemainTime().min(remainingNeedTime);

            if (deductibleTime.compareTo(BigDecimal.ZERO) > 0) {
                // VacationUsageDeduction 생성
                VacationUsageDeduction deduction = VacationUsageDeduction.createVacationUsageDeduction(
                        usage,
                        grant,
                        deductibleTime
                );
                deductionsToSave.add(deduction);

                // VacationGrant의 remainTime 차감
                grant.deduct(deductibleTime);

                remainingNeedTime = remainingNeedTime.subtract(deductibleTime);
            }
        }

        // 차감이 완료되지 않았다면 예외 (이론적으로는 발생하지 않아야 함)
        if (remainingNeedTime.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.notEnoughRemainTime", null, null));
        }

        // 13. 저장
        vacationUsageRepository.save(usage);
        vacationUsageDeductionRepository.saveAll(deductionsToSave);

        log.info("휴가 사용 완료 - User: {}, Period: {} ~ {}, WorkingDays: {}, TotalUseTime: {}",
                user.getId(), data.getStartDate(), data.getEndDate(), betweenDates.size(), totalUseTime);

        return usage.getId();
    }

    /**
     * 유저의 휴가 부여 및 사용 내역 조회
     *
     * @param userId 유저 아이디
     * @return 부여받은 내역(VacationGrant)과 사용한 내역(VacationUsage)
     */
    public VacationServiceDto searchUserVacations(String userId) {
        // 유저 존재 확인
        userService.checkUserExist(userId);

        // 부여받은 내역 조회
        List<VacationGrant> grants = vacationGrantRepository.findByUserId(userId);

        // 사용한 내역 조회
        List<VacationUsage> usages = vacationUsageRepository.findByUserId(userId);

        return VacationServiceDto.builder()
                .grants(grants)
                .usages(usages)
                .build();
    }

    /**
     * 모든 유저의 휴가 부여 및 사용 내역 조회
     *
     * @return 모든 유저별 부여받은 내역(VacationGrant)과 사용한 내역(VacationUsage)
     */
    public List<VacationServiceDto> searchUserGroupVacations() {
        // 모든 부여 내역 조회
        List<VacationGrant> allGrants = vacationGrantRepository.findAllWithUser();

        // 모든 사용 내역 조회
        List<VacationUsage> allUsages = vacationUsageRepository.findAllWithUser();

        // User별로 Grant 그룹핑
        Map<String, List<VacationGrant>> grantsByUser = allGrants.stream()
                .collect(Collectors.groupingBy(g -> g.getUser().getId()));

        // User별로 Usage 그룹핑
        Map<String, List<VacationUsage>> usagesByUser = allUsages.stream()
                .collect(Collectors.groupingBy(u -> u.getUser().getId()));

        // 모든 userId 수집
        Set<String> allUserIds = new HashSet<>();
        allUserIds.addAll(grantsByUser.keySet());
        allUserIds.addAll(usagesByUser.keySet());

        // User별로 VacationServiceDto 생성
        return allUserIds.stream()
                .map(userId -> {
                    List<VacationGrant> grants = grantsByUser.getOrDefault(userId, new ArrayList<>());
                    List<VacationUsage> usages = usagesByUser.getOrDefault(userId, new ArrayList<>());

                    // User 객체는 grants나 usages에서 가져오기
                    User user = null;
                    if (!grants.isEmpty()) {
                        user = grants.get(0).getUser();
                    } else if (!usages.isEmpty()) {
                        user = usages.get(0).getUser();
                    }

                    return VacationServiceDto.builder()
                            .userId(userId)
                            .user(user)
                            .grants(grants)
                            .usages(usages)
                            .build();
                })
                .toList();
    }

    /**
     * 시작 날짜 기준으로 사용 가능한 휴가 조회 (VacationType별 그룹화)
     *
     * @param userId 유저 아이디
     * @param startDate 시작 날짜
     * @return VacationType별로 그룹화된 사용 가능한 휴가 내역
     */
    public List<VacationServiceDto> searcgAvailableVacations(String userId, LocalDateTime startDate) {
        // 유저 조회
        userService.checkUserExist(userId);

        // 시작 날짜를 기준으로 사용 가능한 휴가 부여 내역 조회
        List<VacationGrant> availableGrants = vacationGrantRepository.findAvailableGrantsByUserIdAndDate(userId, startDate);

        // VacationType별로 그룹화하고 remainTime 합산
        Map<VacationType, BigDecimal> remainTimeByType = availableGrants.stream()
                .collect(Collectors.groupingBy(
                        VacationGrant::getType,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                VacationGrant::getRemainTime,
                                BigDecimal::add
                        )
                ));

        // VacationServiceDto 리스트로 변환
        return remainTimeByType.entrySet().stream()
                .map(entry -> VacationServiceDto.builder()
                        .type(entry.getKey())
                        .remainTime(entry.getValue())
                        .build())
                .toList();
    }

    /**
     * 휴가 사용 내역 삭제
     * - VacationUsage를 소프트 삭제
     * - VacationGrant의 remainTime 복구
     *
     * @param vacationUsageId 휴가 사용 내역 ID
     */
    @Transactional
    public void deleteVacationHistory(Long vacationUsageId) {
        // 1. VacationUsage 조회
        VacationUsage usage = vacationUsageRepository.findById(vacationUsageId)
                .orElseThrow(() -> new IllegalArgumentException(ms.getMessage("error.notfound.vacation.usage", null, null)));

        // 2. 이미 삭제된 경우 예외 처리
        if (usage.getIsDeleted() == YNType.Y) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.already.deleted.vacation.usage", null, null));
        }

        // 3. 삭제 가능 시점 체크 (현재 시간이 사용 시작일 이전인지 확인)
        if (PorestTime.isAfterThanEndDate(LocalDateTime.now(), usage.getStartDate())) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.delete.isBeforeThanNow", null, null));
        }

        // 4. VacationUsageDeduction 조회 (차감 내역들)
        List<VacationUsageDeduction> deductions = vacationUsageDeductionRepository.findByUsageId(vacationUsageId);

        // 5. 각 차감 내역에서 차감했던 시간을 VacationGrant에 복구
        for (VacationUsageDeduction deduction : deductions) {
            VacationGrant grant = deduction.getGrant();
            grant.restore(deduction.getDeductedTime());
            log.info("VacationGrant {} 복구: {} 추가", grant.getId(), deduction.getDeductedTime());
        }

        // 6. VacationUsage 소프트 삭제
        usage.deleteVacationUsage();

        log.info("휴가 사용 내역 삭제 완료 - VacationUsage ID: {}, 복구된 차감 내역 수: {}", vacationUsageId, deductions.size());
    }

    /**
     * 기간별 휴가 사용 내역 조회
     *
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 기간 내 모든 사용자의 휴가 사용 내역
     */
    public List<VacationServiceDto> searchPeriodVacationUseHistories(LocalDateTime startDate, LocalDateTime endDate) {
        // 기간에 맞는 휴가 사용 내역 조회 (startDate 기준)
        List<VacationUsage> usages = vacationUsageRepository.findByPeriodWithUser(startDate, endDate);

        // VacationServiceDto로 변환
        return usages.stream()
                .map(usage -> VacationServiceDto.builder()
                        .id(usage.getId())
                        .user(usage.getUser())
                        .desc(usage.getDesc())
                        .timeType(usage.getType())
                        .startDate(usage.getStartDate())
                        .endDate(usage.getEndDate())
                        .usedTime(usage.getUsedTime())
                        .build())
                .toList();
    }

    /**
     * 유저별 기간별 휴가 사용 내역 조회
     *
     * @param userId 유저 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 유저의 기간 내 휴가 사용 내역
     */
    public List<VacationServiceDto> searchUserPeriodVacationUseHistories(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        // 유저 존재 확인
        userService.checkUserExist(userId);

        // 유저의 기간에 맞는 휴가 사용 내역 조회 (startDate 기준)
        List<VacationUsage> usages = vacationUsageRepository.findByUserIdAndPeriodWithUser(userId, startDate, endDate);

        // VacationServiceDto로 변환
        return usages.stream()
                .map(usage -> VacationServiceDto.builder()
                        .id(usage.getId())
                        .desc(usage.getDesc())
                        .timeType(usage.getType())
                        .startDate(usage.getStartDate())
                        .endDate(usage.getEndDate())
                        .usedTime(usage.getUsedTime())
                        .build())
                .toList();
    }

    /**
     * 유저의 월별 휴가 사용 통계 조회
     *
     * @param userId 유저 ID
     * @param year 년도
     * @return 월별 휴가 사용 통계 (1~12월)
     */
    public List<VacationServiceDto> searchUserMonthStatsVacationUseHistories(String userId, String year) {
        // 유저 존재 확인
        userService.checkUserExist(userId);

        // 해당 년도의 1월 1일 ~ 12월 31일 사이의 휴가 사용 내역 조회
        LocalDateTime startDate = LocalDateTime.of(Integer.parseInt(year), 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(Integer.parseInt(year), 12, 31, 23, 59, 59);

        List<VacationUsage> usages = vacationUsageRepository.findByUserIdAndPeriodWithUser(userId, startDate, endDate);

        // 월별 사용량 Map 생성 및 0 초기화 (순서 보장위해 LinkedHashMap 사용)
        Map<Integer, BigDecimal> monthlyMap = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyMap.put(i, BigDecimal.ZERO);
        }

        // 월별 사용량 집계 (startDate의 월 기준)
        for (VacationUsage usage : usages) {
            int month = usage.getStartDate().getMonthValue();
            monthlyMap.merge(month, usage.getUsedTime(), BigDecimal::add);
        }

        return monthlyMap.entrySet().stream()
                .map(e -> VacationServiceDto.builder()
                            .month(e.getKey())
                            .usedTime(e.getValue())
                            .build()
                )
                .toList();
    }

    /**
     * 유저의 휴가 사용 통계 조회 (현재/이전달)
     *
     * @param userId 유저 ID
     * @param baseTime 기준 시간
     * @return 현재 및 이전달 휴가 통계
     */
    public VacationServiceDto searchUserVacationUseStats(String userId, LocalDateTime baseTime) {
        // 유저 존재 확인
        userService.checkUserExist(userId);

        // 현재 통계 계산
        VacationServiceDto curStats = calculateStatsForBaseTime(userId, baseTime);

        // 이전 달 통계 계산
        VacationServiceDto prevStats = calculateStatsForBaseTime(userId, baseTime.minusMonths(1));

        return VacationServiceDto.builder()
                .remainTime(curStats.getRemainTime())
                .usedTime(curStats.getUsedTime())
                .expectUsedTime(curStats.getExpectUsedTime())
                .prevRemainTime(prevStats.getRemainTime())
                .prevUsedTime(prevStats.getUsedTime())
                .prevExpectUsedTime(prevStats.getExpectUsedTime())
                .build();
    }

    /**
     * baseTime 기준 휴가 통계 계산 헬퍼 메서드
     */
    private VacationServiceDto calculateStatsForBaseTime(String userId, LocalDateTime baseTime) {
        // baseTime 기준으로 유효한 VacationGrant 조회
        List<VacationGrant> validGrants = vacationGrantRepository.findValidGrantsByUserIdAndBaseTime(userId, baseTime);

        // 총 부여 시간 계산
        BigDecimal totalGranted = validGrants.stream()
                .map(VacationGrant::getGrantTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // baseTime 이전에 사용한 VacationUsage 조회 및 합산
        List<VacationUsage> usedUsages = vacationUsageRepository.findUsedByUserIdAndBaseTime(userId, baseTime);
        BigDecimal totalUsedTime = usedUsages.stream()
                .map(VacationUsage::getUsedTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // baseTime 이후 사용 예정인 VacationUsage 조회 및 합산
        List<VacationUsage> expectedUsages = vacationUsageRepository.findExpectedByUserIdAndBaseTime(userId, baseTime);
        BigDecimal totalExpectUsedTime = expectedUsages.stream()
                .map(VacationUsage::getUsedTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 잔여 시간 계산
        BigDecimal totalRemainTime = totalGranted.subtract(totalUsedTime);

        return VacationServiceDto.builder()
                .remainTime(totalRemainTime)
                .usedTime(totalUsedTime)
                .expectUsedTime(totalExpectUsedTime)
                .build();
    }

    public VacationUsage checkVacationUsageExist(Long vacationUsageId) {
        Optional<VacationUsage> usage = vacationUsageRepository.findById(vacationUsageId);
        usage.orElseThrow(() -> new IllegalArgumentException(ms.getMessage("error.notfound.vacation.usage", null, null)));
        return usage.get();
    }

    @Transactional
    public Long registVacationPolicy(VacationPolicyServiceDto data) {
        VacationPolicyStrategy strategy = vacationPolicyStrategyFactory.getStrategy(data.getGrantMethod());
        return strategy.registVacationPolicy(data);
    }

    public VacationPolicyServiceDto searchVacationPolicy(Long vacationPolicyId) {
        VacationPolicy policy = checkVacationPolicyExist(vacationPolicyId);

        return VacationPolicyServiceDto.builder()
                .id(policy.getId())
                .name(policy.getName())
                .desc(policy.getDesc())
                .vacationType(policy.getVacationType())
                .grantMethod(policy.getGrantMethod())
                .grantTime(policy.getGrantTime())
                .repeatUnit(policy.getRepeatUnit())
                .repeatInterval(policy.getRepeatInterval())
                .specificMonths(policy.getSpecificMonths())
                .specificDays(policy.getSpecificDays())
                .build();
    }

    public List<VacationPolicyServiceDto> searchVacationPolicies() {
        List<VacationPolicy> policies = vacationPolicyRepository.findVacationPolicies();
        return policies.stream()
                .map(p -> VacationPolicyServiceDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .desc(p.getDesc())
                        .vacationType(p.getVacationType())
                        .grantMethod(p.getGrantMethod())
                        .grantTime(p.getGrantTime())
                        .repeatUnit(p.getRepeatUnit())
                        .repeatInterval(p.getRepeatInterval())
                        .specificMonths(p.getSpecificMonths())
                        .specificDays(p.getSpecificDays())
                        .build())
                .toList();
    }

    public VacationPolicy checkVacationPolicyExist(Long vacationPolicyId) {
        Optional<VacationPolicy> policy = vacationPolicyRepository.findVacationPolicyById(vacationPolicyId);
        policy.orElseThrow(() -> new IllegalArgumentException(ms.getMessage("error.notfound.vacation.policy", null, null)));
        return policy.get();
    }

    /**
     * 휴가 정책 삭제
     *
     * 휴가 정책을 소프트 삭제하고, 구성원에게 부여된 휴가 수량을 처리합니다.
     * - 보유한 휴가에만 영향을 주고, 이미 사용했던 혹은 사용 예정으로 신청해둔 휴가에는 영향을 주지 않습니다.
     *
     * @param vacationPolicyId 삭제할 휴가 정책 ID
     * @return 삭제된 휴가 정책 ID
     */
    @Transactional
    public Long deleteVacationPolicy(Long vacationPolicyId) {
        // 1. 휴가 정책 존재 확인
        VacationPolicy vacationPolicy = checkVacationPolicyExist(vacationPolicyId);

        // 2. 이미 삭제된 정책인지 확인
        if (vacationPolicy.getIsDeleted() == YNType.Y) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.already.deleted.vacation.policy", null, null));
        }

        // 3. 삭제 가능 여부 확인
        if (vacationPolicy.getCanDeleted() == YNType.N) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.cannot.delete.vacation.policy", null, null));
        }

        // 4. 휴가 정책 소프트 삭제
        vacationPolicy.deleteVacationPolicy();

        // 5. 해당 휴가 정책을 사용하는 모든 UserVacationPolicy를 소프트 삭제
        List<UserVacationPolicy> userVacationPolicies = userVacationPolicyRepository.findByVacationPolicyId(vacationPolicyId);
        int deletedUserVacationPolicyCount = 0;

        for (UserVacationPolicy uvp : userVacationPolicies) {
            // 이미 삭제된 경우 스킵
            if (uvp.getIsDeleted() == YNType.Y) {
                continue;
            }

            // UserVacationPolicy 소프트 삭제
            uvp.deleteUserVacationPolicy();
            deletedUserVacationPolicyCount++;
        }

        log.info("Deleted {} user vacation policy assignments for vacation policy {}",
                deletedUserVacationPolicyCount, vacationPolicyId);

        // TODO: 구성원에게 부여된 휴가 수량 처리
        // - 해당 휴가 정책으로 부여된 모든 Vacation 조회
        // - 각 Vacation의 remainTime에서 해당 정책의 grantTime만큼 차감
        // - 단, 이미 사용한 휴가(VacationHistory)에는 영향을 주지 않음
        // - 사용 예정으로 신청해둔 휴가(future VacationHistory)에도 영향을 주지 않음
        log.warn("TODO: Process vacation restore removal for policy {}", vacationPolicyId);

        log.info("Deleted vacation policy {}", vacationPolicyId);

        return vacationPolicyId;
    }

    /**
     * 유저에게 여러 휴가 정책을 일괄 할당
     *
     * @param userId 유저 ID
     * @param vacationPolicyIds 휴가 정책 ID 리스트
     * @return 할당된 휴가 정책 ID 리스트
     */
    @Transactional
    public List<Long> assignVacationPoliciesToUser(String userId, List<Long> vacationPolicyIds) {
        // 1. 유저 존재 확인
        User user = userService.checkUserExist(userId);

        // 2. 할당할 휴가 정책들의 유효성 검증
        List<VacationPolicy> vacationPolicies = new ArrayList<>();
        for (Long policyId : vacationPolicyIds) {
            VacationPolicy policy = checkVacationPolicyExist(policyId);
            vacationPolicies.add(policy);
        }

        // 3. 중복 할당 체크 및 필터링
        List<Long> assignedPolicyIds = new ArrayList<>();
        List<UserVacationPolicy> userVacationPolicies = new ArrayList<>();

        for (VacationPolicy policy : vacationPolicies) {
            // 이미 할당된 정책인지 확인
            boolean alreadyAssigned = userVacationPolicyRepository.existsByUserIdAndVacationPolicyId(userId, policy.getId());

            if (alreadyAssigned) {
                log.warn("User {} already has vacation policy {}, skipping", userId, policy.getId());
                continue;
            }

            // UserVacationPolicy 생성
            UserVacationPolicy userVacationPolicy = UserVacationPolicy.createUserVacationPolicy(user, policy);
            userVacationPolicies.add(userVacationPolicy);
            assignedPolicyIds.add(policy.getId());
        }

        // 4. 일괄 저장
        if (!userVacationPolicies.isEmpty()) {
            userVacationPolicyRepository.saveAll(userVacationPolicies);
            log.info("Assigned {} vacation policies to user {}", userVacationPolicies.size(), userId);
        }

        return assignedPolicyIds;
    }

    /**
     * 유저에게 할당된 휴가 정책 조회
     *
     * @param userId 유저 ID
     * @return 유저에게 할당된 휴가 정책 리스트
     */
    public List<VacationPolicyServiceDto> searchUserVacationPolicies(String userId) {
        // 유저 존재 확인
        userService.checkUserExist(userId);

        // 유저에게 할당된 휴가 정책 조회
        List<UserVacationPolicy> userVacationPolicies = userVacationPolicyRepository.findByUserId(userId);

        return userVacationPolicies.stream()
                .map(uvp -> {
                    VacationPolicy policy = uvp.getVacationPolicy();
                    return VacationPolicyServiceDto.builder()
                            .userVacationPolicyId(uvp.getId())
                            .id(policy.getId())
                            .name(policy.getName())
                            .desc(policy.getDesc())
                            .vacationType(policy.getVacationType())
                            .grantMethod(policy.getGrantMethod())
                            .grantTime(policy.getGrantTime())
                            .repeatUnit(policy.getRepeatUnit())
                            .repeatInterval(policy.getRepeatInterval())
                            .specificMonths(policy.getSpecificMonths())
                            .specificDays(policy.getSpecificDays())
                            .build();
                })
                .toList();
    }

    /**
     * 유저에게 부여된 휴가 정책 회수 (단일)
     *
     * @param userId 유저 ID
     * @param vacationPolicyId 휴가 정책 ID
     * @return 회수된 UserVacationPolicy ID
     */
    @Transactional
    public Long revokeVacationPolicyFromUser(String userId, Long vacationPolicyId) {
        // 1. 유저 존재 확인
        userService.checkUserExist(userId);

        // 2. 휴가 정책 존재 확인
        checkVacationPolicyExist(vacationPolicyId);

        // 3. UserVacationPolicy 조회
        UserVacationPolicy userVacationPolicy = userVacationPolicyRepository
                .findByUserIdAndVacationPolicyId(userId, vacationPolicyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        ms.getMessage("error.notfound.user.vacation.policy", null, null)));

        // 4. 이미 삭제된 경우 예외 처리
        if (userVacationPolicy.getIsDeleted() == YNType.Y) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.already.deleted.user.vacation.policy", null, null));
        }

        // 5. 소프트 삭제 수행
        userVacationPolicy.deleteUserVacationPolicy();

        log.info("Revoked vacation policy {} from user {}", vacationPolicyId, userId);

        return userVacationPolicy.getId();
    }

    /**
     * 유저에게 부여된 여러 휴가 정책 일괄 회수
     *
     * @param userId 유저 ID
     * @param vacationPolicyIds 휴가 정책 ID 리스트
     * @return 회수된 UserVacationPolicy ID 리스트
     */
    @Transactional
    public List<Long> revokeVacationPoliciesFromUser(String userId, List<Long> vacationPolicyIds) {
        // 1. 유저 존재 확인
        userService.checkUserExist(userId);

        List<Long> revokedIds = new ArrayList<>();

        // 2. 각 휴가 정책에 대해 회수 처리
        for (Long policyId : vacationPolicyIds) {
            try {
                // 휴가 정책 존재 확인
                checkVacationPolicyExist(policyId);

                // UserVacationPolicy 조회
                Optional<UserVacationPolicy> optionalUvp = userVacationPolicyRepository
                        .findByUserIdAndVacationPolicyId(userId, policyId);

                if (optionalUvp.isEmpty()) {
                    log.warn("User {} does not have vacation policy {}, skipping", userId, policyId);
                    continue;
                }

                UserVacationPolicy userVacationPolicy = optionalUvp.get();

                // 이미 삭제된 경우 스킵
                if (userVacationPolicy.getIsDeleted() == YNType.Y) {
                    log.warn("Vacation policy {} already revoked from user {}, skipping", policyId, userId);
                    continue;
                }

                // 소프트 삭제 수행
                userVacationPolicy.deleteUserVacationPolicy();
                revokedIds.add(policyId);

            } catch (Exception e) {
                log.error("Failed to revoke vacation policy {} from user {}: {}", policyId, userId, e.getMessage());
                // 일괄 처리 중 개별 에러는 스킵하고 계속 진행
            }
        }

        log.info("Revoked {} vacation policies from user {}", revokedIds.size(), userId);

        return revokedIds;
    }

}
