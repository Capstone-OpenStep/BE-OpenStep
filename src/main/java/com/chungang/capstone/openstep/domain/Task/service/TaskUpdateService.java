package com.chungang.capstone.openstep.domain.Task.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.dto.GithubTaskInfo;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskUpdateService {

	private final TaskRepository taskRepository;

	//github info 기반으로 Task 엔티티 업데이트

	public boolean updateTaskByGithubInfo(Task task, TaskStatus newStatus, GithubTaskInfo githubInfo) {
		boolean needsUpdate = false;

		// 상태 업데이트
		if (task.getStatus() != newStatus) {
			log.info("Updating task status: {} -> {} for task: {}",
				task.getStatus(), newStatus, task.getTaskId());
			task.updateStatus(newStatus);
			needsUpdate = true;
		}

		// PR URL 업데이트
		if (githubInfo.hasPullRequest()) {
			String newPrUrl = githubInfo.getPrUrl();
			if (!Objects.equals(task.getPrUrl(), newPrUrl)) {
				log.info("Updating PR URL for task: {}, new URL: {}",
					task.getTaskId(), newPrUrl);
				task.updatePrUrl(newPrUrl);
				needsUpdate = true;
			}
		}

		// 변경사항이 있을 때만 저장
		if (needsUpdate) {
			taskRepository.save(task);
			log.debug("Task updated and saved: {}", task.getTaskId());
		}

		return needsUpdate;
	}
}
