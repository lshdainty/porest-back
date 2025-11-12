package com.lshdainty.porest.work.service;

import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.user.repository.UserRepositoryImpl;
import com.lshdainty.porest.work.domain.WorkCode;
import com.lshdainty.porest.work.domain.WorkHistory;
import com.lshdainty.porest.work.repository.WorkHistoryRepositoryImpl;
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
    private final WorkHistoryRepositoryImpl workHistoryRepository;
    private final UserRepositoryImpl userRepository;

    @Transactional
    public Long createWorkHistory(WorkHistoryServiceDto data) {
        User user = checkUserExist(data.getUser().getId());

        WorkHistory workHistory = WorkHistory.createWorkHistory(
                data.getDate(),
                user,
                data.getGroup(),
                data.getPart(),
                data.getClasses(),
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
                        .user(w.getUser())
                        .group(w.getGroup())
                        .part(w.getPart())
                        .classes(w.getClasses())
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
                .user(w.getUser())
                .group(w.getGroup())
                .part(w.getPart())
                .classes(w.getClasses())
                .hours(w.getHours())
                .content(w.getContent())
                .build();
    }

    @Transactional
    public void updateWorkHistory(WorkHistoryServiceDto data) {
        WorkHistory workHistory = checkWorkHistoryExist(data.getSeq());
        User user = data.getUser() != null ? checkUserExist(data.getUser().getId()) : null;

        workHistory.updateWorkHistory(
                data.getDate(),
                user,
                data.getGroup(),
                data.getPart(),
                data.getClasses(),
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
        workHistory.orElseThrow(() -> new IllegalArgumentException("업무 이력을 찾을 수 없습니다."));
        return workHistory.get();
    }

    private User checkUserExist(String userId) {
        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user.get();
    }
}
