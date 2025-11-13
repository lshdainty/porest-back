package com.lshdainty.porest.work.service;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.user.service.UserService;
import com.lshdainty.porest.work.domain.WorkCode;
import com.lshdainty.porest.work.domain.WorkHistory;
import com.lshdainty.porest.work.repository.WorkCodeRepositoryImpl;
import com.lshdainty.porest.work.repository.WorkHistoryCustomRepositoryImpl;
import com.lshdainty.porest.work.service.dto.WorkHistoryServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WorkHistoryService {
    private final MessageSource ms;
    private final WorkHistoryCustomRepositoryImpl workHistoryRepository;
    private final WorkCodeRepositoryImpl workCodeRepository;
    private final UserService userService;

    @Transactional
    public Long createWorkHistory(WorkHistoryServiceDto data) {
        User user = userService.checkUserExist(data.getUserId());
        WorkCode group = checkWorkCodeExist(data.getGroupCode());
        WorkCode part = checkWorkCodeExist(data.getPartCode());
        WorkCode classes = checkWorkCodeExist(data.getClassCode());

        WorkHistory workHistory = WorkHistory.createWorkHistory(
                data.getDate(),
                user,
                group,
                part,
                classes,
                data.getHours(),
                data.getContent()
        );
        workHistoryRepository.save(workHistory);
        return workHistory.getSeq();
    }

    public List<WorkHistoryServiceDto> findAllWorkHistories() {
        List<WorkHistory> workHistories = workHistoryRepository.findAll();

        return workHistories.stream()
                .map(w -> WorkHistoryServiceDto.builder()
                        .seq(w.getSeq())
                        .date(w.getDate())
                        .userId(w.getUser().getId())
                        .userName(w.getUser().getName())
                        .groupName(w.getGroup().getName())
                        .partName(w.getPart().getName())
                        .className(w.getDivision().getName())
                        .hours(w.getHours())
                        .content(w.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    public WorkHistoryServiceDto findWorkHistory(Long seq) {
        WorkHistory w = checkWorkHistoryExist(seq);

        return WorkHistoryServiceDto.builder()
                .seq(w.getSeq())
                .date(w.getDate())
                .userId(w.getUser().getId())
                .userName(w.getUser().getName())
                .groupName(w.getGroup().getName())
                .partName(w.getPart().getName())
                .className(w.getDivision().getName())
                .hours(w.getHours())
                .content(w.getContent())
                .build();
    }

    @Transactional
    public void updateWorkHistory(WorkHistoryServiceDto data) {
        WorkHistory workHistory = checkWorkHistoryExist(data.getSeq());
        User user = userService.checkUserExist(data.getUserId());
        WorkCode group = checkWorkCodeExist(data.getGroupCode());
        WorkCode part = checkWorkCodeExist(data.getPartCode());
        WorkCode classes = checkWorkCodeExist(data.getClassCode());

        workHistory.updateWorkHistory(
                data.getDate(),
                user,
                group,
                part,
                classes,
                data.getHours(),
                data.getContent()
        );
    }

    @Transactional
    public void deleteWorkHistory(Long seq) {
        WorkHistory workHistory = checkWorkHistoryExist(seq);
        workHistoryRepository.delete(workHistory);
    }

    private WorkHistory checkWorkHistoryExist(Long seq) {
        Optional<WorkHistory> workHistory = workHistoryRepository.findById(seq);
        workHistory.orElseThrow(() -> new IllegalArgumentException(ms.getMessage("error.notfound.work.history", null, null)));
        return workHistory.get();
    }

    private WorkCode checkWorkCodeExist(String code) {
        if (code == null) {
            throw new IllegalArgumentException(ms.getMessage("error.validate.work.code.required", null, null));
        }
        Optional<WorkCode> workCode = workCodeRepository.findByCode(code);
        workCode.orElseThrow(() -> new IllegalArgumentException(ms.getMessage("error.notfound.work.code", null, null)));
        return workCode.get();
    }
}
