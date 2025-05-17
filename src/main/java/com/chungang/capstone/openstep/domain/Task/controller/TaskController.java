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

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Tag(name = "테스크(기여) API", description = "테스크(기여) 관련 API입니다.")
public class TaskController {

	private final TaskQueryService taskQueryService;

	@GetMapping("/{task-id}")
	public ApiResponse<TaskResponseDTO.TaskDetail> getTaskDetail(
		@PathVariable("task-id") Long taskId
	) {
		Member member= SecurityUtils.getCurrentMember();
		Task task=taskQueryService.getTaskById(taskId,member);

		return ApiResponse.onSuccess(SuccessStatus.TASK_GET_OK, TaskConverter.toTaskDetail(task));
	}

}
