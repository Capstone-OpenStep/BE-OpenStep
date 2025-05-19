package com.chungang.capstone.openstep.domain.Task.dto;

import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import lombok.Builder;

public class TaskResponseDTO {
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
}
