package com.chungang.capstone.openstep.domain.achievement.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chungang.capstone.openstep.domain.Member.entity.Member;
import com.chungang.capstone.openstep.domain.Member.repository.MemberRepository;
import com.chungang.capstone.openstep.domain.Task.dto.TaskResponseDTO;
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
				MemberAchievement memberAchievement = MemberAchievement.create(member, type);
				return memberAchievementRepository.save(memberAchievement);
			});
	}

	@Transactional
	@Override
	public void unlock(Long memberId, AchievementType type) {
		MemberAchievement memberAchievement = findOrCreateAchievement(memberId, type);

		if (!memberAchievement.isUnlocked()) {
			memberAchievement.unlock();
			// cascade로 인해 자동 저장됨
		}
	}

	@Override
	public void incrementProgress(Long memberId, AchievementType type) {
		MemberAchievement memberAchievement = findOrCreateAchievement(memberId, type);
		memberAchievement.incrementProgress();
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

	// 특정 Task로 달성한 업적들 조회
	public List<TaskResponseDTO.AchievementDTO> getAchievementsByTask(Long memberId, Long taskId) {
		List<MemberAchievement> achievements = memberAchievementRepository.findByMemberIdAndTriggerTaskId(memberId, taskId);

		return achievements.stream()
			.map(this::convertToTaskAchievementDTO)
			.collect(Collectors.toList());
	}

	// 해당 Task와 관련된 모든 업적 (달성한 것 + 진행 중인 것)
	public List<TaskResponseDTO.AchievementDTO> getRelatedAchievements(Long memberId, Long taskId) {

		// 1. 이 Task로 달성한 업적
		List<TaskResponseDTO.AchievementDTO> taskAchievements = getAchievementsByTask(memberId, taskId);

		// 2. 최근 5분 내 달성한 업적들 (방금 이 Task로 달성했을 가능성)
		List<TaskResponseDTO.AchievementDTO> recentAchievements = getRecentAchievementsByMember(memberId, Duration.ofMinutes(5));

		// 3. 중복 제거하고 합치기
		Set<Long> taskAchievementIds = taskAchievements.stream()
			.map(TaskResponseDTO.AchievementDTO::getId)
			.collect(Collectors.toSet());

		List<TaskResponseDTO.AchievementDTO> combined = new ArrayList<>(taskAchievements);
		recentAchievements.stream()
			.filter(achievement -> !taskAchievementIds.contains(achievement.getId()))
			.forEach(combined::add);

		return combined;
	}

	private List<TaskResponseDTO.AchievementDTO> getRecentAchievementsByMember(Long memberId, Duration within) {
		LocalDateTime since = LocalDateTime.now().minus(within);
		List<MemberAchievement> recentAchievements = memberAchievementRepository.findByMemberIdAndUnlockedAtAfter(memberId, since);

		return recentAchievements.stream()
			.map(achievement -> convertToTaskAchievementDTO(achievement).toBuilder()
				.isNewlyUnlocked(true) // 최근 달성한 것으로 마킹
				.build())
			.collect(Collectors.toList());
	}

	private TaskResponseDTO.AchievementDTO convertToTaskAchievementDTO(MemberAchievement achievement) {
		return TaskResponseDTO.AchievementDTO.builder()
			.id(achievement.getId())
			.type(achievement.getType().name())
			.title(achievement.getType().getTitle())
			.description(achievement.getType().getDescription())
			.unlocked(achievement.isUnlocked())
			.unlockedAt(achievement.getUnlockedAt())
			.currentProgress(achievement.getCurrentProgress())
			.targetCount(achievement.getType().getTargetCount())
			.isNewlyUnlocked(false) // 기본값
			.build();
	}
}
