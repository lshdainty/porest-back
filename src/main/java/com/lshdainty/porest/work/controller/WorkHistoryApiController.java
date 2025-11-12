package com.lshdainty.porest.work.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.user.domain.User;
import com.lshdainty.porest.work.controller.dto.WorkHistoryApiDto;
import com.lshdainty.porest.work.domain.WorkCode;
import com.lshdainty.porest.work.service.WorkHistoryService;
import com.lshdainty.porest.work.service.dto.WorkHistoryServiceDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WorkHistoryApiController {
    private final WorkHistoryService workHistoryService;
    private final EntityManager em;

    @PostMapping("/api/v1/work-histories")
    public ApiResponse createWorkHistory(@RequestBody WorkHistoryApiDto.CreateWorkHistoryReq data) {
        User user = em.getReference(User.class, data.getWorkUser());
        WorkCode group = em.getReference(WorkCode.class, data.getWorkGroup());
        WorkCode part = em.getReference(WorkCode.class, data.getWorkPart());
        WorkCode classes = em.getReference(WorkCode.class, data.getWorkClass());

        Long workHistorySeq = workHistoryService.createWorkHistory(WorkHistoryServiceDto.builder()
                .date(data.getWorkDate())
                .user(user)
                .group(group)
                .part(part)
                .classes(classes)
                .hours(data.getWorkHour())
                .content(data.getWorkContent())
                .build()
        );
        return ApiResponse.success(new WorkHistoryApiDto.CreateWorkHistoryResp(workHistorySeq));
    }

    @GetMapping("/api/v1/work-histories")
    public ApiResponse findAllWorkHistories() {
        List<WorkHistoryServiceDto> dtos = workHistoryService.findAllWorkHistories();
        return ApiResponse.success(dtos.stream()
                .map(w -> new WorkHistoryApiDto.WorkHistoryResp(
                        w.getSeq(),
                        w.getDate(),
                        w.getUser() != null ? w.getUser().getName() : null,
                        w.getGroup() != null ? w.getGroup().getName() : null,
                        w.getPart() != null ? w.getPart().getName() : null,
                        w.getClasses() != null ? w.getClasses().getName() : null,
                        w.getHours(),
                        w.getContent()
                ))
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/v1/work-histories/{seq}")
    public ApiResponse findWorkHistory(@PathVariable("seq") Long seq) {
        WorkHistoryServiceDto w = workHistoryService.findWorkHistory(seq);
        return ApiResponse.success(new WorkHistoryApiDto.WorkHistoryResp(
                w.getSeq(),
                w.getDate(),
                w.getUser() != null ? w.getUser().getName() : null,
                w.getGroup() != null ? w.getGroup().getName() : null,
                w.getPart() != null ? w.getPart().getName() : null,
                w.getClasses() != null ? w.getClasses().getName() : null,
                w.getHours(),
                w.getContent()
        ));
    }

    @PutMapping("/api/v1/work-histories/{seq}")
    public ApiResponse updateWorkHistory(@PathVariable("seq") Long seq, @RequestBody WorkHistoryApiDto.UpdateWorkHistoryReq data) {
        User user = data.getWorkUser() != null ? em.getReference(User.class, data.getWorkUser()) : null;
        WorkCode group = data.getWorkGroup() != null ? em.getReference(WorkCode.class, data.getWorkGroup()) : null;
        WorkCode part = data.getWorkPart() != null ? em.getReference(WorkCode.class, data.getWorkPart()) : null;
        WorkCode classes = data.getWorkClass() != null ? em.getReference(WorkCode.class, data.getWorkClass()) : null;

        workHistoryService.updateWorkHistory(WorkHistoryServiceDto.builder()
                .seq(seq)
                .date(data.getWorkDate())
                .user(user)
                .group(group)
                .part(part)
                .classes(classes)
                .hours(data.getWorkHour())
                .content(data.getWorkContent())
                .build()
        );
        return ApiResponse.success();
    }

    @DeleteMapping("/api/v1/work-histories/{seq}")
    public ApiResponse deleteWorkHistory(@PathVariable("seq") Long seq) {
        workHistoryService.deleteWorkHistory(seq);
        return ApiResponse.success();
    }
}
