package com.chungang.capstone.openstep.domain.achievement.event;

import java.time.LocalDate;

import com.chungang.capstone.openstep.domain.Task.entity.TaskStatus;

import lombok.Getter;

@Getter
public class TaskActivityEvent extends AchievementEvent {
	private final Long taskId;
	private final TaskStatus oldStatus;
	private final TaskStatus newStatus;
	private final LocalDate activityDate;

	public TaskActivityEvent(Long userId, Long taskId, TaskStatus oldStatus, TaskStatus newStatus) {
		super(userId);
		this.taskId = taskId;
		this.oldStatus = oldStatus;
		this.newStatus = newStatus;
		this.activityDate = LocalDate.now();
	}
}
