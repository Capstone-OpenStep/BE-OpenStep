package com.chungang.capstone.openstep.domain.Task.converter;

import java.util.List;

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

	public static TaskResponseDTO.Status toTaskStatus(Task task) {
		return TaskResponseDTO.Status.builder().
				taskId(task.getTaskId()).
				status(task.getStatus()).
				createdAt(task.getCreatedAt()).
				updatedAt(task.getUpdatedAt()).
				build();
	}

	public static TaskResponseDTO.TaskBrief toTaskBrief(Task task) {
		return TaskResponseDTO.TaskBrief.builder()
				.taskId(task.getTaskId())
				.title(task.getIssue().getTitle())
				.branchName(task.getBranchName())
				.status(task.getStatus())
				.createdAt(task.getCreatedAt().toString())
				.updatedAt(task.getUpdatedAt().toString())
				.build();
	}

	public static List<TaskResponseDTO.TaskBrief> taskToTaskBriefs(List<Task> updatedTasks) {
		return updatedTasks.stream()
			.map(TaskConverter::toTaskBrief)
			.toList();
	}
}
