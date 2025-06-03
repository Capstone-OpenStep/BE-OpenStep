package com.chungang.capstone.openstep.domain.Task.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@Operation(summary = "특정 테스크의 PR URL 업데이트 API", description = "특정 오픈소스 레포지토리의 기여(테스크) PR URL을 업데이트합니다. forked 상태 이외의 상태에서만 가능합니다.")
	@PatchMapping("/{task-id}/pr")
	public ApiResponse<TaskResponseDTO.TaskDetail> updatePRUrl (
		@PathVariable Long taskId,
		@RequestParam(name = "url") String prUrl ){
		Member member = SecurityUtils.getCurrentMember();
		TaskResponseDTO.TaskDetail taskDetail = taskQueryService.updatePRUrl(taskId, prUrl, member);
		return ApiResponse.onSuccess(SuccessStatus.TASK_PR_URL_UPDATE_OK, taskDetail);
	}

	@GetMapping("/recent")
	@Operation(summary = "테스크 목록 레포별 조회 API", description = "기여(테스크) 목록을 레포지토리 단위로 묶어서 반환합니다. 테스크가 최신인 순서로 정렬됩니다.")
	public ApiResponse<List<TaskResponseDTO.RepoTaskGroupDTO>> getTaskList() {
		Member member = SecurityUtils.getCurrentMember();
		return ApiResponse.onSuccess(SuccessStatus.TASK_GET_OK, taskQueryService.getRepoTaskGroup(member));
	}

	//모든 테스크의 상태를 업데이트하고 상태가 변한 테스크를 반환하는 API
	@GetMapping("/update-status")
	@Operation(summary = "모든 테스크 상태 업데이트 API", description = "모든 기여(테스크)의 상태를 업데이트하고, 상태가 변경된 테스크 목록을 반환합니다.")
	public ApiResponse<List<TaskResponseDTO.TaskBrief>> updateTaskStatus() {
		Member member = SecurityUtils.getCurrentMember();
		List<Task> updatedTasks = taskQueryService.updateAllTaskStatus(member);
		if(updatedTasks.isEmpty()) {
			return ApiResponse.onSuccess(SuccessStatus.TASK_STATUS_UPDATE_OK, List.of());
		}
		return ApiResponse.onSuccess(SuccessStatus.TASK_STATUS_UPDATE_OK, TaskConverter.taskToTaskBriefs(updatedTasks));
	}

	@GetMapping("/statistics")
	@Operation(summary = "테스크 통계 조회 API", description = "사용자의 기여(테스크) 통계를 조회합니다. 라벨 별로 기여 수를 반환합니다.(feature, bug, refactor, goot first issue, chore 로 구분되고, 나머지 라벨은 other로 분류됩니다.)")
	public ApiResponse<Map<String, Long>> getTaskStatistics() {
		Member member = SecurityUtils.getCurrentMember();
		Map<String, Long> statistics = taskQueryService.getTaskStatistics(member);
		return ApiResponse.onSuccess(SuccessStatus.TASK_STATISTICS_GET_OK, statistics);
	}


}
