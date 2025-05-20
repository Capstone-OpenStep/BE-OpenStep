package com.chungang.capstone.openstep.domain.Task.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.converter.TaskConverter;
import com.chungang.capstone.openstep.domain.Task.dto.TaskResponseDTO;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.service.TaskQueryService;
import com.chungang.capstone.openstep.global.apiPayload.ApiResponse;
import com.chungang.capstone.openstep.global.apiPayload.code.status.SuccessStatus;
import com.chungang.capstone.openstep.global.security.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Tag(name = "테스크(기여) API", description = "테스크(기여) 관련 API입니다.")
public class TaskController {

	private final TaskQueryService taskQueryService;

	@Operation(summary = "특정 테스크 상세 조회 API", description = "특정 오픈소스 레포지토리의 기여(테스크) 정보를 조회합니다.")
	@GetMapping("/{task-id}")
	public ApiResponse<TaskResponseDTO.TaskDetail> getTaskDetail(
		@PathVariable("task-id") Long taskId
	) {
		Member member= SecurityUtils.getCurrentMember();
		return ApiResponse.onSuccess(SuccessStatus.TASK_GET_OK, taskQueryService.getTaskDetailById(taskId,member));
	}

	@Operation(summary = "특정 테스크의 브랜치 이름 조회 API", description = "특정 오픈소스 레포지토리의 기여(테스크) 브랜치 이름을 조회합니다.")
	@GetMapping("/{task-id}/branch")
	public ApiResponse<TaskResponseDTO.TaskBranchName> getTaskBranch(
		@PathVariable("task-id") Long taskId
	) {
		Member member= SecurityUtils.getCurrentMember();

		return ApiResponse.onSuccess(SuccessStatus.TASK_BRANCH_GET_OK, taskQueryService.getBranchNameByTask(taskId,member));
	}
	@Operation(summary = "특정 테스크의 기여 상태 조회 API", description = "특정 오픈소스 레포지토리의 기여(테스크) 상태를 조회합니다.")
	@GetMapping("/{task-id}/status")
	public ApiResponse<TaskResponseDTO.Status> getTaskStatus(
		@PathVariable("task-id") Long taskId
	) {
		Member member= SecurityUtils.getCurrentMember();
		return ApiResponse.onSuccess(SuccessStatus.TASK_BRANCH_GET_OK, taskQueryService.getStatusByTaskId(taskId,member));
	}

}
