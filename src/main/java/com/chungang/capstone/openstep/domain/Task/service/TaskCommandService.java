package com.chungang.capstone.openstep.domain.Task.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.dto.PullRequestResponse;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.converter.TaskConverter;
import com.chungang.capstone.openstep.domain.Task.dto.TaskResponseDTO;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.TaskException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskCommandService {
	private final TaskRepository taskRepository;
	public void updatePrUrl(Task task, PullRequestResponse.PullRequestRes pr) {
		try {
			String prUrl = pr.url();
			if (!Objects.equals(task.getPrUrl(), prUrl)) {
				task.updatePrUrl(prUrl);
				log.info("Updated PR URL for task: {}, PR URL: {}", task.getTaskId(), prUrl);
			}
		} catch (Exception e) {
			log.warn("Failed to update PR URL for task: {}", task.getTaskId(), e);
		}
		taskRepository.save(task);
	}
	public TaskResponseDTO.Status updateTaskStatusToProgress(Long taskId, Member member) {
		Task task = taskRepository.findById(taskId).orElseThrow(() ->
			new TaskException(ErrorStatus.TASK_NOT_FOUND));

		if(task.getStatus()!= TaskStatus.FORKED) {
			throw new TaskException(ErrorStatus.TASK_STATUS_UPDATE_FORBIDDEN);
		}
		task.updateStatus(TaskStatus.PROGRESS);
		taskRepository.save(task);

		return TaskConverter.toTaskStatus(task);
	}
}
