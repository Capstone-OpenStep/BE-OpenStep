package com.chungang.capstone.openstep.domain.Task.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import lombok.Builder;

public class TaskResponseDTO {

	@Builder
	public record Status(
			Long taskId,
			TaskStatus status,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {
	}

	@Builder
	public record TaskDetail(
			Long taskId,
			String title,
			String forkedUrl,
			TaskStatus status,
			String branchName,
			String createdAt,
			String updatedAt,
			Long issueId,
			String issueUrl
	) {
	}

	@Builder
	public record TaskBranchName(
			String branchName

	) {
	}
	public record RepoTaskGroupDTO(
		String repository,
		List<TaskBrief> tasks
	) {}

	@Builder
	public record TaskBrief(
			Long taskId,
			String title,
			TaskStatus status,
			String createdAt,
			String updatedAt
	) {
	}
}
