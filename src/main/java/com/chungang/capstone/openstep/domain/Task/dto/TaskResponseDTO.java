package com.chungang.capstone.openstep.domain.Task.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import lombok.Builder;
import lombok.Getter;

public class TaskResponseDTO {

	@Builder
	public record Status(
			Long taskId,
			TaskStatus status,
			LocalDateTime createdAt,
			LocalDateTime updatedAt
	) {
	}

	@Builder(toBuilder = true)
	public record TaskDetail(
			Long taskId,
			String title,
			String forkedUrl,
			TaskStatus status,
			String branchName,
			String createdAt,
			String updatedAt,
			Long issueId,
			String issueUrl,
			String prUrl,
			List<AchievementDTO> achievements
	) {
		public static TaskDetail of(
			Long taskId,
			String title,
			String forkedUrl,
			TaskStatus status,
			String branchName,
			String createdAt,
			String updatedAt,
			Long issueId,
			String issueUrl,
			String prUrl
		) {
			return new TaskDetail(
				taskId, title, forkedUrl, status, branchName,
				createdAt, updatedAt, issueId, issueUrl,prUrl,
				new ArrayList<>()
			);
		}

		// 업적 정보와 함께 새로운 인스턴스 생성하는 메소드
		public TaskDetail withAchievements(List<AchievementDTO> achievements) {
			return new TaskDetail(
				taskId, title, forkedUrl, status, branchName,
				createdAt, updatedAt, issueId, issueUrl,prUrl,
				achievements != null ? achievements : new ArrayList<>()
			);
		}
	}
	// 업적 정보 DTO
	@Getter
	@Builder(toBuilder = true)
	public static class AchievementDTO {
		private Long id;
		private String type;
		private String title;
		private String description;
		private boolean unlocked;
		private LocalDateTime unlockedAt;
		private int currentProgress;
		private int targetCount;

		@Builder.Default
		private final boolean isNewlyUnlocked = false;
	}

	@Builder
	public record TaskBranchName(
			String branchName

	) {
	}
	@Builder
	public record RepoTaskGroupDTO(
		Long repoId,
		String repository,
		String description,
		String language,
		Integer starCount,
		LocalDateTime last_github_update,
		List<TaskBrief> tasks
	) {}

	@Builder
	public record TaskBrief(
			Long taskId,
			String title,
			TaskStatus status,
			String branchName,
			String createdAt,
			String updatedAt
	) {
	}
}
