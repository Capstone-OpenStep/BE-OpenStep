package com.chungang.capstone.openstep.domain.Task.converter;

import com.chungang.capstone.openstep.domain.Task.dto.TaskResponseDTO;
import com.chungang.capstone.openstep.domain.Task.entity.Task;

public class TaskConverter {
	public static TaskResponseDTO.TaskDetail toTaskDetail(Task task){
		return TaskResponseDTO.TaskDetail.builder().
				taskId(task.getTaskId()).
				status(task.getStatus()).
				forkedUrl(task.getForkedUrl()).
				createdAt(task.getCreatedAt().toString()).
				branchName(task.getBranchName()).
				updatedAt(task.getUpdatedAt().toString()).
				issueId(task.getIssue().getIssueId()).
				issueUrl(task.getIssue().getGithubUrl()).
				build();
	}
	public static TaskResponseDTO.TaskBranchName toTaskBranchName(Task task){
		return TaskResponseDTO.TaskBranchName.builder().
				branchName(task.getBranchName()).
				build();
	}
}
