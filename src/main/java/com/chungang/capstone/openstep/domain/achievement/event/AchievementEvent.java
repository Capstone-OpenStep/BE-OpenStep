package com.chungang.capstone.openstep.domain.achievement.event;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class AchievementEvent {
	//기본 이벤트
	private final Long memberId;
	private final LocalDateTime occurredAt;

	public AchievementEvent(Long memberId) {
		this.memberId = memberId;
		this.occurredAt = LocalDateTime.now();
	}
}
