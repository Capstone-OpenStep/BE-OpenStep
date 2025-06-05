package com.chungang.capstone.openstep.domain.achievement.service;

import java.util.List;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.achievement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.achievement.enums.AchievementType;

public interface AchievementService {

	public MemberAchievement findOrCreateAchievement(
		Long memberId,
		AchievementType type
	);

	public void unlock(
		Long memberId,
		AchievementType type
	);

	public void incrementProgress(
		Long memberId,
		AchievementType type
	);

	List<MemberAchievement> getMemberUnlockedAchievements(Long memberId);

	public Member getMember(Long memberId);

	boolean hasUnlockedAchievement(Long memberId, AchievementType achievementType);
}
