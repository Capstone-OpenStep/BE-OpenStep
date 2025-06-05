package com.chungang.capstone.openstep.domain.achievement.event;

import lombok.Getter;

@Getter
public class PrCreatedEvent extends AchievementEvent {
	private final Long taskId;
	private final boolean hasBugKeywords;
	private final String title;
	private final String description;

	public PrCreatedEvent(Long memberId, Long taskId, String title, String description) {
		super(memberId);
		this.taskId = taskId;
		this.hasBugKeywords = containsBugKeywords(title,description);
		this.title = title;
		this.description = description;
	}

	private boolean containsBugKeywords(String title, String description) {
		String combined = (title + " " + description).toLowerCase();
		return combined.contains("bug") ||
			combined.contains("fix") ||
			combined.contains("error")||
			combined.contains("버그") ||
			combined.contains("문제");
	}
}
