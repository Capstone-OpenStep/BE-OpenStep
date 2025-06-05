package com.chungang.capstone.openstep.domain.achievement.event;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class TaskCompletedEvent extends AchievementEvent {
	private final Long taskId;
	private final String repoName;
	private final LocalDateTime completedAt;

	public TaskCompletedEvent(Long memberId, Long taskId, String repoName ) {
		super(memberId);
		this.taskId = taskId;
		this.repoName = repoName;
		this.completedAt = LocalDateTime.now();
	}

}
