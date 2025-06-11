package com.chungang.capstone.openstep.domain.Task.service;

import java.util.*;
import java.util.stream.Collectors;

import com.chungang.capstone.openstep.domain.Github.dto.GithubTaskInfo;
import com.chungang.capstone.openstep.domain.Github.service.GithubInfoService;
import com.chungang.capstone.openstep.domain.Rank.repository.TaskXpLogRepository;
import com.chungang.capstone.openstep.domain.Rank.service.RankCommandService;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Github.service.GitHubStatusResolverService;
import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Repo.entity.Repo;
import com.chungang.capstone.openstep.domain.Task.converter.TaskConverter;
import com.chungang.capstone.openstep.domain.Task.dto.TaskResponseDTO;
import com.chungang.capstone.openstep.domain.Task.entity.Task;
import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;
import com.chungang.capstone.openstep.domain.Task.repository.TaskRepository;
import com.chungang.capstone.openstep.domain.achievement.service.AchievementService;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.TaskException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskQueryService {

	private final TaskRepository taskRepository;
	private final RankCommandService rankCommandService;
	private final TaskXpLogRepository taskXpLogRepository;
	private final AchievementService achievementService;
	private final GithubInfoService gitHubInfoService;
	private final TaskStatusResolver taskStatusResolver;
	private final TaskUpdateService taskUpdateService;
	private final TaskEventService taskEventService;

	@Transactional
	public TaskResponseDTO.TaskDetail getTaskDetailById(Long taskId, Member member) {
		Task task = syncTaskWithGitHub(taskId, member);
		TaskResponseDTO.TaskDetail taskDetail = TaskConverter.toTaskDetail(task);

		// 이 Task와 관련된 업적 정보 조회
		List<TaskResponseDTO.AchievementDTO> relatedAchievements = achievementService.getRelatedAchievements(
			member.getMemberId(),
			taskId
		);

		return taskDetail.withAchievements(relatedAchievements);
	}

	public TaskResponseDTO.TaskBranchName getBranchNameByTask(Long taskId, Member member) {
		Task task = taskRepository.findById(taskId).orElseThrow(() ->
			new TaskException(ErrorStatus.TASK_NOT_FOUND));
		return TaskConverter.toTaskBranchName(task);
	}

	@Transactional
	public TaskResponseDTO.Status getStatusByTaskId(Long taskId, Member member) {
		Task task = syncTaskWithGitHub(taskId, member);
		return TaskConverter.toTaskStatus(task);
	}

	// public Map<String , List<TaskResponseDTO.TaskBrief>> getTaskListGroupedByRepo(Member member) {
	// 	List<Task> taskList = taskRepository.findAllByMember(member);
	// 	//repository 별로 모으기
	//
	// 	// 1. 그룹핑
	// 	Map<String , List<Task>> grouped = taskList.stream()
	// 		.collect(Collectors.groupingBy(task -> task.getIssue().getRepo().getRepoName()));
	//
	// 	// 2. 그룹을 최신 Task 기준으로 정렬
	// 	return grouped.entrySet().stream()
	// 		.sorted((e1, e2) -> {
	// 			// 각 그룹 내에서 가장 최신 Task의 updatedAt 비교
	// 			Task latest1 = e1.getValue().stream().max((a, b) -> a.getUpdatedAt().compareTo(b.getUpdatedAt())).orElse(null);
	// 			Task latest2 = e2.getValue().stream().max((a, b) -> a.getUpdatedAt().compareTo(b.getUpdatedAt())).orElse(null);
	// 			if (latest1 == null || latest2 == null) return 0;
	// 			return latest2.getUpdatedAt().compareTo(latest1.getUpdatedAt()); // 최신순
	// 		})
	// 		.collect(Collectors.toMap(
	// 			Map.Entry::getKey,
	// 			e -> e.getValue().stream()
	// 				.sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
	// 				.map(TaskConverter::toTaskBrief)
	// 				.toList(),
	// 			(a, b) -> a,
	// 			LinkedHashMap::new // insertion order 유지
	// 		));
	// }

	public List<TaskResponseDTO.RepoTaskGroupDTO> getRepoTaskGroup(Member member) {

		List<Task> taskList = taskRepository.findAllByMember(member);

		//레포 끼리 그룹핑
		Map<Repo, List<Task>> groupedTasks = taskList.stream()
			.collect(Collectors.groupingBy(task -> task.getIssue().getRepo()));

		//레포의 github 업데이트 시간 기준으로 정렬
		return groupedTasks.entrySet().stream()
			.sorted((e1, e2) -> {
				Repo repo1 = e1.getKey();
				Repo repo2 = e2.getKey();
				return repo2.getLastGithubUpdate().compareTo(repo1.getLastGithubUpdate());
			})
			.map(entry -> {
				Repo repo = entry.getKey();
				List<TaskResponseDTO.TaskBrief> taskBriefs = entry.getValue().stream()
					.map(TaskConverter::toTaskBrief)
					.collect(Collectors.toList());
				return TaskResponseDTO.RepoTaskGroupDTO.builder()
					.repoId(repo.getRepoId())
					.repository(repo.getRepoName())
					.description(repo.getDescription())
					.language(repo.getLanguage())
					.starCount(repo.getStars())
					.last_github_update(repo.getLastGithubUpdate())
					.tasks(taskBriefs)
					.build();
			})
			.collect(Collectors.toList());
	}

//	public List<Task> updateAllTaskStatus(Member member) {
//		List<Task> tasks = taskRepository.findAllByMember(member);
//		List<Task> updatedTasks = tasks.stream()
//			.map(task -> {
//				TaskStatus resolvedStatus = githubStatusResolver.resolveStatus(task, member);
//				if (task.getStatus() != resolvedStatus) {
//					task.updateStatus(resolvedStatus);
//					return Optional.of(taskRepository.save(task));
//				}
//				return Optional.<Task>empty();
//			})
//			.flatMap(Optional::stream) // Optional 중 값 있는 것만 추출
//			.collect(Collectors.toList());
//
//		return updatedTasks;
//	}

	@Transactional
	public List<Task> updateAllTaskStatus(Member member) {
		List<Task> tasks = taskRepository.findAllByMember(member);

		//github 동기화
		return tasks.stream()
				.map(task -> {
					TaskStatus oldStatus = task.getStatus();
					//github 동기화
					GithubTaskInfo githubInfo = gitHubInfoService.getGithubTaskInfo(task, member);
					TaskStatus newStatus = taskStatusResolver.resolveStatus(task, githubInfo);

					boolean updated = taskUpdateService.updateTaskByGithubInfo(task, newStatus, githubInfo);

					if (updated && oldStatus != newStatus) {
						taskEventService.publishStatusChangeEvent(member, task, oldStatus, newStatus);
						return Optional.of(task);
					}
					return Optional.<Task>empty();
				})
				.flatMap(Optional::stream)
				.collect(Collectors.toList());
	}

	public Map<String, Long> getTaskStatistics(Member member) {
		List<Task> tasks = taskRepository.findAllByMember(member);

		// 라벨 별로 기여 수를 세기
		Map<String, Long> statistics = tasks.stream()
			.flatMap(task -> task.getIssue().getLabels().stream())
			.collect(Collectors.groupingBy(label -> label, Collectors.counting()));

		// 라벨을 feature, bug, refactor, good first issue, chore로 구분하고 나머지는 other로 분류
		Map<String, Long> categorizedStatistics = new LinkedHashMap<>();
		categorizedStatistics.put("feature", statistics.getOrDefault("feature", 0L));
		categorizedStatistics.put("bug", statistics.getOrDefault("bug", 0L));
		categorizedStatistics.put("refactor", statistics.getOrDefault("refactor", 0L));
		categorizedStatistics.put("good first issue", statistics.getOrDefault("good first issue", 0L));
		categorizedStatistics.put("chore", statistics.getOrDefault("chore", 0L));
		categorizedStatistics.put("other", statistics.entrySet().stream()
			.filter(entry -> !categorizedStatistics.containsKey(entry.getKey()))
			.map(Map.Entry::getValue)
			.reduce(0L, Long::sum));

		return categorizedStatistics;
	}

	@Deprecated
	public TaskResponseDTO.TaskDetail updatePRUrl(Long taskId,String prUrl ,Member member) {
		Task task = syncTaskWithGitHub(taskId, member);
		task.updatePrUrl(prUrl);
		taskRepository.save(task);
		return TaskConverter.toTaskDetail(task);
	}
	private Task syncTaskWithGitHub(Long taskId, Member member) {
		Task task = taskRepository.findById(taskId).orElseThrow(() ->
			new TaskException(ErrorStatus.TASK_NOT_FOUND));

		TaskStatus oldStatus = task.getStatus();
		//github 동기화
		GithubTaskInfo githubInfo = gitHubInfoService.getGithubTaskInfo(task, member);
		TaskStatus newStatus = taskStatusResolver.resolveStatus(task, githubInfo);

		boolean updated = taskUpdateService.updateTaskByGithubInfo(task, newStatus, githubInfo);

		if (updated && oldStatus != newStatus) {
			taskEventService.publishStatusChangeEvent(member, task, oldStatus, newStatus);
		}
		return task;
	}
}
