package com.chungang.capstone.openstep.domain.achievement.event;

import lombok.Getter;

@Getter
public class CommentAddedEvent extends AchievementEvent {
	//기본 이벤트
	private final Long prId;
	private final String comment;
	private final boolean hasPositivewords;

	public CommentAddedEvent(Long memberId, Long prId, String comment) {
		super(memberId);
		this.prId = prId;
		this.comment = comment;
		this.hasPositivewords = containsPositiveWords(comment);
	}

	private boolean containsPositiveWords(String comment) {
		String lowerComment = comment.toLowerCase();
		return lowerComment.contains("good") ||
			lowerComment.contains("great") ||
			lowerComment.contains("excellent") ||
			lowerComment.contains("좋네요") ||
			lowerComment.contains("좋습니다") ||
			lowerComment.contains("좋아요");
	}
}
