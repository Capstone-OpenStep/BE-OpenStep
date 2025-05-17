package com.chungang.capstone.openstep.domain.Task.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.service.GitHubStatusResolverService;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.TaskException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskQueryService {

	private final TaskRepository taskRepository;
	private final GitHubStatusResolverService githubStatusResolver;

	public Task getTaskById(Long taskId, Member member) {
		Task task = taskRepository.findById(taskId).orElseThrow(() ->
			new TaskException(ErrorStatus.TASK_NOT_FOUND));
		TaskStatus resolvedStatus = githubStatusResolver.resolveStatus(task, member);

		// DB 캐시 상태가 다르면 update
		if (task.getStatus() != resolvedStatus) {
			task.updateStatus(resolvedStatus);
			taskRepository.save(task);
		}
		return task;
	}
}
