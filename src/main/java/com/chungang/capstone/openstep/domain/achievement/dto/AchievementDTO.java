package com.chungang.capstone.openstep.domain.achievement.dto;

import java.time.LocalDateTime;

import com.chungang.capstone.openstep.domain.achievement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.achievement.enums.AchievementType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AchievementDTO {
	private Long id;
	private AchievementType type;
	private String title;
	private String description;
	private int currentProgress;
	private int targetCount;
	private boolean unlocked;
	private LocalDateTime unlockedAt;

	// 트리거 Task 정보 추가
	private Long triggerTaskId;
	private String triggerTaskTitle;
	private String triggerRepoName;
	private String triggerTaskUrl; // GitHub Issue URL

	public static AchievementDTO from(MemberAchievement memberAchievement) {
		return AchievementDTO.builder()
			.id(memberAchievement.getId())
			.type(memberAchievement.getType())
			.title(memberAchievement.getType().getTitle())
			.description(memberAchievement.getType().getDescription())
			.currentProgress(memberAchievement.getCurrentProgress())
			.targetCount(memberAchievement.getType().getTargetCount())
			.unlocked(memberAchievement.isUnlocked())
			.unlockedAt(memberAchievement.getUnlockedAt())

			// 트리거 Task 정보
			.triggerTaskId(memberAchievement.getTriggerTask() != null ?
				memberAchievement.getTriggerTask().getTaskId() : null)
			.triggerTaskTitle(memberAchievement.getTriggerTaskTitle())
			.triggerRepoName(memberAchievement.getTriggerRepoName())
			.triggerTaskUrl(memberAchievement.getTriggerTask() != null ?
				memberAchievement.getTriggerTask().getIssue().getGithubUrl() : null)
			.build();
	}


}
