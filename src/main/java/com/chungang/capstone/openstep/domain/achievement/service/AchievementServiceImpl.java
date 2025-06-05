package com.chungang.capstone.openstep.domain.achievement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.achievement.entity.MemberAchievement;
import com.chungang.capstone.openstep.domain.achievement.enums.AchievementType;
import com.chungang.capstone.openstep.domain.achievement.repository.MemberAchievementRepository;
import com.chungang.capstone.openstep.global.apiPayload.code.status.ErrorStatus;
import com.chungang.capstone.openstep.global.apiPayload.exception.handler.MemberHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

	private final MemberAchievementRepository memberAchievementRepository;
	private final MemberRepository memberRepository;

	@Transactional
	@Override
	public MemberAchievement findOrCreateAchievement(Long memberId, AchievementType type) {
		return memberAchievementRepository.findByMemberIdAndType(memberId,type)
			.orElseGet(() -> {
				Member member = memberRepository.findById(memberId)
					.orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
				MemberAchievement achievement = MemberAchievement.create(member, type);
				return memberAchievementRepository.save(achievement);
			});
	}

	@Transactional
	@Override
	public void unlock(Long memberId, AchievementType type) {
		MemberAchievement achievement = findOrCreateAchievement(memberId, type);

		if (!achievement.isUnlocked()) {
			achievement.unlock();
			// cascade로 인해 자동 저장됨
		}
	}

	@Override
	public void incrementProgress(Long memberId, AchievementType type) {
		MemberAchievement achievement = findOrCreateAchievement(memberId, type);
		achievement.incrementProgress();
	}

	@Override
	public List<MemberAchievement> getMemberUnlockedAchievements(Long memberId) {
		Member member = getMember(memberId);
		return member.getUnLockedAchievements();
	}

	@Override
	public boolean hasUnlockedAchievement(Long memberId, AchievementType achievementType) {
		return memberAchievementRepository.findByMemberIdAndType(memberId, achievementType)
			.map(MemberAchievement::isUnlocked)
			.orElse(false);
	}

	@Override
	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
	}
}
