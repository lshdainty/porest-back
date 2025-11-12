package com.lshdainty.porest.work.controller;

import com.lshdainty.porest.common.controller.ApiResponse;
import com.lshdainty.porest.work.controller.dto.WorkHistoryApiDto;
import com.lshdainty.porest.work.service.WorkHistoryService;
import com.lshdainty.porest.work.service.dto.WorkHistoryServiceDto;
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

    @PostMapping("/api/v1/work-histories")
    public ApiResponse createWorkHistory(@RequestBody WorkHistoryApiDto.CreateWorkHistoryReq data) {
        Long workHistorySeq = workHistoryService.createWorkHistory(WorkHistoryServiceDto.builder()
                .date(data.getWorkDate())
                .userId(data.getWorkUserId())
                .groupCode(data.getWorkGroupCode())
                .partCode(data.getWorkPartCode())
                .classCode(data.getWorkClassCode())
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
                        w.getUserName(),
                        w.getGroupName(),
                        w.getPartName(),
                        w.getClassName(),
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
                w.getUserName(),
                w.getGroupName(),
                w.getPartName(),
                w.getClassName(),
                w.getHours(),
                w.getContent()
        ));
    }

    @PutMapping("/api/v1/work-histories/{seq}")
    public ApiResponse updateWorkHistory(@PathVariable("seq") Long seq, @RequestBody WorkHistoryApiDto.UpdateWorkHistoryReq data) {
        workHistoryService.updateWorkHistory(WorkHistoryServiceDto.builder()
                .seq(seq)
                .date(data.getWorkDate())
                .userId(data.getWorkUserId())
                .groupCode(data.getWorkGroupCode())
                .partCode(data.getWorkPartCode())
                .classCode(data.getWorkClassCode())
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
