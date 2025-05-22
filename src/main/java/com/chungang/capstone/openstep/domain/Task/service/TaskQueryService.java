package com.chungang.capstone.openstep.domain.Task.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.service.GitHubStatusResolverService;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Task.converter.TaskConverter;
import com.chungang.capstone.openstep.domain.Task.dto.TaskResponseDTO;
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

	public TaskResponseDTO.TaskDetail getTaskDetailById(Long taskId, Member member) {
		Task task = taskRepository.findById(taskId).orElseThrow(() ->
			new TaskException(ErrorStatus.TASK_NOT_FOUND));
		TaskStatus resolvedStatus = githubStatusResolver.resolveStatus(task, member);

		// DB 캐시 상태가 다르면 update
		if (task.getStatus() != resolvedStatus) {
			task.updateStatus(resolvedStatus);
			taskRepository.save(task);
		}
		return TaskConverter.toTaskDetail(task);
	}

	public TaskResponseDTO.TaskBranchName getBranchNameByTask(Long taskId, Member member) {
		Task task = taskRepository.findById(taskId).orElseThrow(() ->
			new TaskException(ErrorStatus.TASK_NOT_FOUND));
		return TaskConverter.toTaskBranchName(task);
	}

	public TaskResponseDTO.Status getStatusByTaskId(Long taskId, Member member) {
		Task task = taskRepository.findById(taskId).orElseThrow(() ->
			new TaskException(ErrorStatus.TASK_NOT_FOUND));
		TaskStatus resolvedStatus = githubStatusResolver.resolveStatus(task, member);

		// DB 캐시 상태가 다르면 update
		if (task.getStatus() != resolvedStatus) {
			task.updateStatus(resolvedStatus);
			taskRepository.save(task);
		}
		return TaskConverter.toTaskStatus(task);
	}

	public Map<String , List<TaskResponseDTO.TaskBrief>> getTaskListGroupedByRepo(Member member) {
		List<Task> taskList = taskRepository.findAllByMember(member);
		//repository 별로 모으기

		// 1. 그룹핑
		Map<String , List<Task>> grouped = taskList.stream()
			.collect(Collectors.groupingBy(task -> task.getIssue().getRepo().getRepoName()));

		// 2. 그룹을 최신 Task 기준으로 정렬
		return grouped.entrySet().stream()
			.sorted((e1, e2) -> {
				// 각 그룹 내에서 가장 최신 Task의 updatedAt 비교
				Task latest1 = e1.getValue().stream().max((a, b) -> a.getUpdatedAt().compareTo(b.getUpdatedAt())).orElse(null);
				Task latest2 = e2.getValue().stream().max((a, b) -> a.getUpdatedAt().compareTo(b.getUpdatedAt())).orElse(null);
				if (latest1 == null || latest2 == null) return 0;
				return latest2.getUpdatedAt().compareTo(latest1.getUpdatedAt()); // 최신순
			})
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				e -> e.getValue().stream()
					.sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
					.map(TaskConverter::toTaskBrief)
					.toList(),
				(a, b) -> a,
				LinkedHashMap::new // insertion order 유지
			));
	}
}
